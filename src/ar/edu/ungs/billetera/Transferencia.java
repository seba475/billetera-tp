package ar.edu.ungs.billetera;

public class Transferencia extends Actividad {
    
    private Cuenta cuentaDestino;
    private double monto;
    
    public Transferencia(Cuenta cuentaOrigen, Cuenta cuentaDestino,
    		double monto, boolean aprobada) {
    	
        super(cuentaOrigen, aprobada);
        this.cuentaDestino = cuentaDestino;
        this.monto = monto;
    }
    
    @Override
    public String toString() {
        return "fecha: " + obtenerFechaHora() + "\n" +
               "origen: " + obtenerCuentaOrigen().obtenerTitular().obtenerDni() + 
               " (" + obtenerCuentaOrigen().obtenerCvu() + ")\n" +
               "destino: " + cuentaDestino.obtenerTitular().obtenerDni() + 
               " (" + cuentaDestino.obtenerCvu() + ")\n" +
               "monto: " + monto + "\n" +
               (fueAprobada() ? "Aprobado" : "Rechazado");
    }
}