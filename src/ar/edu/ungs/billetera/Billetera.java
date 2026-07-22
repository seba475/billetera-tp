package ar.edu.ungs.billetera;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Billetera implements IBilletera {

	private Map<String, Usuario> usuarios;
	private List<Actividad> historial;
	private Map<Integer, Inversion> inversiones;
	private Map<String, Empresa> empresas;
	private Map<String, Cuenta> cuentasPorCvu;
	private int proximoIdInversion;
    
	public Billetera() {
	    this.usuarios = new HashMap<>();
	    this.historial = new ArrayList<>();
	    this.inversiones = new HashMap<>();
	    this.empresas = new HashMap<>();
	    this.cuentasPorCvu = new HashMap<>();
	    this.proximoIdInversion = 1;
	}
	
	private Usuario buscarUsuarioPorDni(String dni) {
	    Usuario u = usuarios.get(dni);
	    if (u == null) {
	        throw new IllegalArgumentException("Usuario con DNI " + dni + " no está registrado");
	    }
	    return u;
	}

	private Cuenta buscarCuentaPorCvu(String cvu) {
	    Cuenta c = cuentasPorCvu.get(cvu);
	    if (c == null) {
	        throw new IllegalArgumentException("Cuenta con CVU " + cvu + " no está registrada");
	    }
	    return c;
	}

    private Empresa buscarEmpresaPorCuit(String cuit) {
        Empresa e = empresas.get(cuit);
        if (e == null) {
            throw new IllegalArgumentException("Empresa con CUIT " + cuit + " no está registrada");
        }
        return e;
    }
    
    private void validarAliasDisponible(String alias) {
        for (Usuario u : usuarios.values()) {
            if (u.buscarCvuPorAlias(alias) != null) {
                throw new IllegalArgumentException("El alias ya está registrado");
            }
        }
    }
	
    @Override
    public void registrarEmpresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
        if (empresas.containsKey(cuit)) {
            throw new IllegalArgumentException("La empresa ya existe");
        }
        empresas.put(cuit, new Empresa(cuit, nombreFantasia, telefono, email, nombreContacto));
    }

    @Override
	public void agregarPersonaAutorizada(String cuitEmpresa, String dniAutorizado) {
    	Empresa emp = buscarEmpresaPorCuit(cuitEmpresa);
        emp.agregarAutorizado(dniAutorizado);
	}

    @Override
    public void registrarUsuario(String dni, String nombre, String telefono, String email) {
        if (usuarios.containsKey(dni)) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        usuarios.put(dni, new Usuario(dni, nombre, telefono, email));
    }

	@Override
	public String crearCuentaRegular(String dni, String alias) {
	    Usuario u = buscarUsuarioPorDni(dni);
	    validarAliasDisponible(alias);
	    
	    CuentaRegular c = new CuentaRegular(u, Utilitarios.generarSiguienteCvu(), alias);
	    u.agregarCuenta(c);
	    cuentasPorCvu.put(c.obtenerCvu(), c);
	    
	    return c.obtenerCvu();
	}

	@Override
	public String crearCuentaPremium(String dni, String alias, double saldoInicial) {
	    Usuario u = buscarUsuarioPorDni(dni);
	    validarAliasDisponible(alias);
	    
	    CuentaPremium c = new CuentaPremium(u, Utilitarios.generarSiguienteCvu(), alias, saldoInicial);
	    u.agregarCuenta(c);
	    cuentasPorCvu.put(c.obtenerCvu(), c);
	    
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
	    cuentasPorCvu.put(c.obtenerCvu(), c);
	    
	    return c.obtenerCvu();
	}

	public List<String> obtenerCuentas(String dni) {
	    Usuario u = buscarUsuarioPorDni(dni);
	    return u.listarCuentas();
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

	    Transferencia t = new Transferencia(origen, destino, monto, aprobada);
	    historial.add(t);

	    if (aprobada) {
	        origen.obtenerTitular().debitarDe(origen, monto);
	        destino.obtenerTitular().acreditarEn(destino, monto);
	    }
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
	        usuario.invertirEn(cuenta, monto);
	        inversiones.put(idAsignado, inversion);
	    }
	    
	    RegistroInversion registro = new RegistroInversion(cuenta, inversion, aprobada);
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
	        usuario.invertirEn(cuenta, monto);
	        inversiones.put(idAsignado, inversion);
	    }
	    
	    RegistroInversion registro = new RegistroInversion(cuenta, inversion, aprobada);
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
	        usuario.invertirEn(cuenta, monto);
	        inversiones.put(idAsignado, inversion);
	    }
	    
	    RegistroInversion registro = new RegistroInversion(cuenta, inversion, aprobada);
	    historial.add(registro);
	    
	    return idAsignado;
	}
	
	@Override
	public void precancelarInversion(String dni, String cvu, int idInversion) {
	    Usuario usuario = buscarUsuarioPorDni(dni);
	    Cuenta cuenta = buscarCuentaPorCvu(cvu);

	    Inversion inversion = inversiones.get(idInversion);
	    
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

	    double aDevolver = inversion.calcularDevolucionPorPrecancelacion(Utilitarios.hoy());
	    
	    usuario.recibirDevolucion(cuenta, aDevolver, inversion.obtenerMonto());
	}
	
	public String consultarCvu(String alias) {
	    for (Usuario u : usuarios.values()) {
	        String cvu = u.buscarCvuPorAlias(alias);
	        if (cvu != null) {
	            return cvu;
	        }
	    }
	    throw new IllegalArgumentException("Alias inexistente");
	}

	@Override
	public List<String> consultarHistorialGlobal() {
	    List<String> resultado = new ArrayList<>();
	    Iterator<Actividad> it = historial.iterator();
	    while (it.hasNext()) {
	        Actividad a = it.next();
	        resultado.add(a.toString());
	    }
	    return resultado;
	}
	
	@Override
	public List<String> consultarHistorialCuenta(String cvu) {
	    Cuenta cuenta = buscarCuentaPorCvu(cvu);
	    List<String> resultado = new ArrayList<>();
	    for (Actividad a : historial) {
	        if (a.involucraCuenta(cuenta)) {
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
	        if (a.involucraUsuario(usuario)) {
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
	    for (Usuario u : usuarios.values()) {
	        u.aportarCuentas(todas);
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

	    sb.append("Usuarios registrados:\n");
	    for (Usuario u : usuarios.values()) {
	        sb.append(u.detalleConCuentas());
	    }

	    sb.append("Empresas registradas:\n");
	    for (Empresa e : empresas.values()) {
	        sb.append(e.toString()).append("\n");
	    }

	    sb.append("Inversiones:\n");
	    for (Inversion i : inversiones.values()) {
	        sb.append("ID ").append(i.obtenerId())
	          .append(": ").append(i.toString())
	          .append(" - Monto: $").append(i.obtenerMonto())
	          .append(" - Plazo: ").append(i.obtenerPlazoEnDias()).append(" dias");
	        if (i.estaPrecancelada()) {
	            sb.append(" (precancelada)");
	        }
	        sb.append("\n");
	    }

	    sb.append("Historial:\n");
	    for (Actividad a : historial) {
	        sb.append(a.toString()).append("\n");
	    }

	    return sb.toString(); 
	}
}