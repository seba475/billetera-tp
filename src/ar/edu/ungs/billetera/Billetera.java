package ar.edu.ungs.billetera;

import java.util.List;
import java.util.ArrayList;

public class Billetera implements IBilletera {

	private List<Usuario> usuarios;
    private List<Actividad> historial;
    private List<Inversion> inversiones;
    private List<Empresa> empresas;
    private int proximoIdInversion;
    
    public Billetera() {
        this.usuarios = new ArrayList<>();
        this.historial = new ArrayList<>();
        this.inversiones = new ArrayList<>();
        this.empresas = new ArrayList<>();
        this.proximoIdInversion = 1;
    }
	
    private Usuario buscarUsuarioPorDni(String dni) {
        for (Usuario u : usuarios) {
            if (u.obtenerDni().equals(dni)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Usuario con DNI " + dni + " no está registrado");
    }

    private Cuenta buscarCuentaPorCvu(String cvu) {
        for (Usuario u : usuarios) {
            for (Cuenta c : u.consultarCuentas()) {
                if (c.obtenerCvu().equals(cvu)) {
                    return c;
                }
            }
        }
        throw new IllegalArgumentException("Cuenta con CVU " + cvu + " no está registrada");
    }

    private Empresa buscarEmpresaPorCuit(String cuit) {
        for (Empresa e : empresas) {
            if (e.obtenerCuit().equals(cuit)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Empresa con CUIT " + cuit + " no está registrada");
    }
    
    private void validarAliasDisponible(String alias) {
        for (Usuario u : usuarios) {
            for (Cuenta c : u.consultarCuentas()) {
                if (c.obtenerAlias().equals(alias)) {
                    throw new IllegalArgumentException("El alias ya está registrado");
                }
            }
        }
    }
	
    @Override
    public void registrarEmpresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
        for (Empresa e : empresas) {
            if (e.obtenerCuit().equals(cuit)) {
                throw new IllegalArgumentException("La empresa ya existe");
            }
        }
        empresas.add(new Empresa(cuit, nombreFantasia, telefono, email, nombreContacto));
    }

    @Override
	public void agregarPersonaAutorizada(String cuitEmpresa, String dniAutorizado) {
        Empresa emp = buscarEmpresaPorCuit(cuitEmpresa);
        emp.agregarAutorizado(dniAutorizado);
	}

	@Override
	public void registrarUsuario(String dni, String nombre, String telefono, String email) {
	    for (Usuario u : usuarios) {
	        if (u.obtenerDni().equals(dni)) {
	            throw new IllegalArgumentException("El usuario ya existe");
	        }
	    }
	    usuarios.add(new Usuario(dni, nombre, telefono, email));
	}

	@Override
	public String crearCuentaRegular(String dni, String alias) {
	    Usuario u = buscarUsuarioPorDni(dni);
	    validarAliasDisponible(alias);
	    
	    CuentaRegular c = new CuentaRegular(u, Utilitarios.generarSiguienteCvu(), alias);
	    u.agregarCuenta(c);
	    
	    return c.obtenerCvu();
	}

	@Override
	public String crearCuentaPremium(String dni, String alias, double saldoInicial) {
	    Usuario u = buscarUsuarioPorDni(dni);
	    validarAliasDisponible(alias);
	    
	    CuentaPremium c = new CuentaPremium(u, Utilitarios.generarSiguienteCvu(), alias, saldoInicial);
	    u.agregarCuenta(c);
	    
	    return c.obtenerCvu();
	}

	@Override
	public String crearCuentaCorporativa(String dni, String alias, String cuit) {
	    Usuario u = buscarUsuarioPorDni(dni);
	    Empresa e = buscarEmpresaPorCuit(cuit);
	    validarAliasDisponible(alias);
	    
	    if (!e.estaAutorizado(dni)) {
	        throw new IllegalArgumentException("Usuario no autorizado");
	    }
	    
	    CuentaCorporativa c = new CuentaCorporativa(u, Utilitarios.generarSiguienteCvu(), alias, e);
	    u.agregarCuenta(c);
	    
	    return c.obtenerCvu();
	}

	@Override
	public List<String> obtenerCuentas(String dni) {
	    Usuario u = buscarUsuarioPorDni(dni);
	    List<String> resultado = new ArrayList<>();
	    for (Cuenta c : u.consultarCuentas()) {
	        resultado.add(c.toString());
	    }
	    return resultado;
	}

	@Override
	public double obtenerSaldoDisponible(String cvu) {
	    Cuenta c = buscarCuentaPorCvu(cvu);
	    return c.obtenerSaldo();
	}

	@Override
	public void realizarTransferencia(String cvuOrigen, String cvuDestino, double monto) {
	    Cuenta origen = buscarCuentaPorCvu(cvuOrigen);
	    Cuenta destino = buscarCuentaPorCvu(cvuDestino);
	    
	    boolean aprobada = origen.validarOperacion(monto, origen.obtenerTitular());
	    
	    if (aprobada) {
	        origen.debitar(monto);
	        destino.acreditar(monto);
	    }
	    
	    Transferencia t = new Transferencia(origen, destino, monto, aprobada);
	    historial.add(t);
	}

	@Override
	public int realizarInversionRentaFija(String dni, String cvu, double monto, int plazoDias) {
	    Usuario usuario = buscarUsuarioPorDni(dni);
	    Cuenta cuenta = buscarCuentaPorCvu(cvu);
	    
	    if (cuenta.obtenerTitular() != usuario) {
	        throw new IllegalArgumentException("La cuenta no pertenece al usuario");
	    }
	    
	    boolean aprobada = cuenta.validarOperacion(monto, usuario);
	    
	    int idAsignado = proximoIdInversion;
	    proximoIdInversion++;
	    
	    RentaFija inversion = new RentaFija(idAsignado, cuenta, monto, plazoDias);
	    
	    if (aprobada) {
	        cuenta.debitar(monto);
	        usuario.sumarInvertido(monto);
	        inversiones.add(inversion);
	    }
	    
	    RegistroInversion registro = new RegistroInversion(cuenta, inversion, "INICIO", aprobada);
	    historial.add(registro);
	    
	    return idAsignado;
	}

	@Override
	public int realizarInversionDivisa(String dni, String cvu, double monto, int plazoDias, String divisa, double tasa) {
	    Usuario usuario = buscarUsuarioPorDni(dni);
	    Cuenta cuenta = buscarCuentaPorCvu(cvu);
	    
	    if (cuenta.obtenerTitular() != usuario) {
	        throw new IllegalArgumentException("La cuenta no pertenece al usuario");
	    }
	    
	    boolean aprobada = cuenta.validarOperacion(monto, usuario);
	    
	    int idAsignado = proximoIdInversion;
	    proximoIdInversion++;
	    
	    VinculadaDivisa inversion = new VinculadaDivisa(idAsignado, cuenta, monto, plazoDias, divisa, tasa);
	    
	    if (aprobada) {
	        cuenta.debitar(monto);
	        usuario.sumarInvertido(monto);
	        inversiones.add(inversion);
	    }
	    
	    RegistroInversion registro = new RegistroInversion(cuenta, inversion, "INICIO", aprobada);
	    historial.add(registro);
	    
	    return idAsignado;
	}

	@Override
	public int realizarInversionLiquidez(String dni, String cvu, double monto, int plazoDias) {
	    Usuario usuario = buscarUsuarioPorDni(dni);
	    Cuenta cuenta = buscarCuentaPorCvu(cvu);
	    
	    if (cuenta.obtenerTitular() != usuario) {
	        throw new IllegalArgumentException("La cuenta no pertenece al usuario");
	    }
	    
	    if (!(cuenta instanceof CuentaCorporativa)) {
	        throw new IllegalArgumentException("FondoLiquidez solo se puede crear desde una CuentaCorporativa");
	    }
	    
	    boolean aprobada = cuenta.validarOperacion(monto, usuario);
	    
	    int idAsignado = proximoIdInversion;
	    proximoIdInversion++;
	    
	    FondoLiquidez inversion = new FondoLiquidez(idAsignado, (CuentaCorporativa) cuenta, monto, plazoDias);
	    
	    if (aprobada) {
	        cuenta.debitar(monto);
	        usuario.sumarInvertido(monto);
	        inversiones.add(inversion);
	    }
	    
	    RegistroInversion registro = new RegistroInversion(cuenta, inversion, "INICIO", aprobada);
	    historial.add(registro);
	    
	    return idAsignado;
	}

	@Override
	public void precancelarInversion(String dni, String cvu, int idInversion) {
	    Usuario usuario = buscarUsuarioPorDni(dni);
	    Cuenta cuenta = buscarCuentaPorCvu(cvu);

	    Inversion inversion = null;
	    for (Inversion i : inversiones) {
	        if (i.obtenerId() == idInversion) {
	            inversion = i;
	        }
	    }
	    
	    if (inversion == null) {
	        throw new IllegalArgumentException("La inversión no existe");
	    }
	    
	    if (inversion.obtenerCuentaOrigen() != cuenta) {
	        throw new IllegalArgumentException("La inversión no pertenece a esa cuenta");
	    }
	    
	    if (cuenta.obtenerTitular() != usuario) {
	        throw new IllegalArgumentException("La cuenta no pertenece al usuario");
	    }
	    
	    if (inversion.estaPrecancelada()) {
	        throw new IllegalArgumentException("La inversión ya está precancelada");
	    }
	    
	    inversion.precancelar();
	    
	    double aDevolver;
	    
	    if (inversion instanceof VinculadaDivisa) {
	        VinculadaDivisa vd = (VinculadaDivisa) inversion;
	        double divisasInvertidas = vd.obtenerMonto() / vd.obtenerCotizacionInicial();
	        double intereses = vd.calcularResultadoHasta(Utilitarios.hoy()) / 2;
	        double cotizacionActual = Utilitarios.consultarCotizacion(vd.obtenerDivisa());
	        aDevolver = (divisasInvertidas * cotizacionActual) + intereses;
	    } else {
	        double intereses = inversion.calcularResultadoHasta(Utilitarios.hoy());
	        aDevolver = inversion.obtenerMonto() + (intereses / 2);
	    }
	    
	    cuenta.acreditar(aDevolver);
	    usuario.restarInvertido(inversion.obtenerMonto());
	    
	    RegistroInversion registro = new RegistroInversion(cuenta, inversion, "PRECANCELACION", true);
	    historial.add(registro);
	}

	@Override
	public String consultarCvu(String alias) {
	    for (Usuario u : usuarios) {
	        for (Cuenta c : u.consultarCuentas()) {
	            if (c.obtenerAlias().equals(alias)) {
	                return c.obtenerCvu();
	            }
	        }
	    }
	    throw new IllegalArgumentException("Alias inexistente");
	}

	@Override
	public List<String> consultarHistorialGlobal() {
	    List<String> resultado = new ArrayList<>();
	    for (Actividad a : historial) {
	        resultado.add(a.toString());
	    }
	    return resultado;
	}

	@Override
	public List<String> consultarHistorialCuenta(String cvu) {
	    Cuenta cuenta = buscarCuentaPorCvu(cvu);
	    List<String> resultado = new ArrayList<>();
	    for (Actividad a : historial) {
	        if (a.obtenerCuentaOrigen() == cuenta) {
	            resultado.add(a.toString());
	        }
	    }
	    return resultado;
	}

	@Override
	public List<String> consultarHistorialUsuario(String dniUsuario) {
	    Usuario usuario = buscarUsuarioPorDni(dniUsuario);
	    List<String> resultado = new ArrayList<>();
	    for (Actividad a : historial) {
	        if (a.obtenerCuentaOrigen().obtenerTitular() == usuario) {
	            resultado.add(a.toString());
	        }
	    }
	    return resultado;
	}

	@Override
	public double obtenerTotalInvertido(String dniUsuario) {
	    Usuario usuario = buscarUsuarioPorDni(dniUsuario);
	    return usuario.obtenerTotalInvertido();
	}

	@Override
	public List<String> cuentasConMayorVolumen(int cantidadTop) {
	    if (cantidadTop <= 0) {
	        throw new IllegalArgumentException("La cantidad debe ser positiva");
	    }
	    
	    // Juntar todas las cuentas
	    List<Cuenta> todas = new ArrayList<>();
	    for (Usuario u : usuarios) {
	        todas.addAll(u.consultarCuentas());
	    }
	    
	    // Ordenar de mayor a menor por cantidadTransacciones (Ordenamiento burbuja)
	    for (int i = 0; i < todas.size() - 1; i++) {
	        for (int j = 0; j < todas.size() - 1 - i; j++) {
	            if (todas.get(j).obtenerCantidadTransacciones() < todas.get(j + 1).obtenerCantidadTransacciones()) {
	                Cuenta temp = todas.get(j);
	                todas.set(j, todas.get(j + 1));
	                todas.set(j + 1, temp);
	            }
	        }
	    }
	    
	    // Tomar los primeros cantidadTop y convertir a Strings
	    List<String> resultado = new ArrayList<>();
	    int limite = Math.min(cantidadTop, todas.size());
	    for (int i = 0; i < limite; i++) {
	        resultado.add(todas.get(i).toString());
	    }
	    return resultado;
	}
	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("=== BILLETERA ===\n");
	    
	    sb.append("\nUsuarios registrados (").append(usuarios.size()).append("):\n");
	    for (Usuario u : usuarios) {
	        sb.append("  - ").append(u.toString()).append("\n");
	        for (Cuenta c : u.consultarCuentas()) {
	            sb.append("      ").append(c.toString()).append(" - Saldo: $").append(c.obtenerSaldo()).append("\n");
	        }
	    }
	    
	    sb.append("\nEmpresas registradas (").append(empresas.size()).append("):\n");
	    for (Empresa e : empresas) {
	        sb.append("  - ").append(e.toString()).append("\n");
	    }
	    
	    sb.append("\nInversiones activas (").append(inversiones.size()).append("):\n");
	    for (Inversion i : inversiones) {
	        sb.append("  - ID ").append(i.obtenerId()).append(": ").append(i.toString())
	          .append(" - Monto: $").append(i.obtenerMonto())
	          .append(" - Plazo: ").append(i.obtenerPlazoEnDias()).append(" días")
	          .append(i.estaPrecancelada() ? " (PRECANCELADA)" : "")
	          .append("\n");
	    }
	    
	    sb.append("\nHistorial de actividades (").append(historial.size()).append("):\n");
	    for (Actividad a : historial) {
	        sb.append("  ----------------------\n");
	        sb.append(a.toString()).append("\n");
	    }
	    
	    return sb.toString();
	}
}