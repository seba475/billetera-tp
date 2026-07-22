package ar.edu.ungs.billetera;

public class Transferencia extends Actividad {
    
    private Cuenta cuentaDestino;
    private double monto;
    
    public Transferencia(Cuenta cuentaOrigen, Cuenta cuentaDestino,
            double monto, boolean aprobada) {

        super(cuentaOrigen, aprobada);
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        this.cuentaDestino = cuentaDestino;
        this.monto = monto;
    }
    
    public Cuenta obtenerCuentaDestino() {
        return cuentaDestino;
    }
    
    @Override
    public boolean involucraCuenta(Cuenta cuenta) {
        return super.involucraCuenta(cuenta) || cuentaDestino == cuenta;
    }
    
    @Override
    public boolean involucraUsuario(Usuario usuario) {
        return super.involucraUsuario(usuario) || cuentaDestino.obtenerTitular() == usuario;
    }
    
    @Override
    public String toString() {
        return "fecha: " + obtenerFecha() + "\n" +
               "origen: " + obtenerCuentaOrigen().obtenerTitular().obtenerDni() + 
               " (" + obtenerCuentaOrigen().obtenerCvu() + ")\n" +
               "destino: " + cuentaDestino.obtenerTitular().obtenerDni() + 
               " (" + cuentaDestino.obtenerCvu() + ")\n" +
               "monto: " + monto + "\n" +
               (fueAprobada() ? "Aprobado" : "Rechazado");
    }
}