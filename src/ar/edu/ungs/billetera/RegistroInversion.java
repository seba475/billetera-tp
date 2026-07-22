package ar.edu.ungs.billetera;

public class RegistroInversion extends Actividad {
    
    private Inversion inversion;
    
    public RegistroInversion(Cuenta cuentaOrigen, Inversion inversion, boolean aprobada) {
        super(cuentaOrigen, aprobada);
        this.inversion = inversion;
    }
    
    @Override
    public String toString() {
        return "fecha: " + obtenerFecha() + "\n" +
               "origen: " + obtenerCuentaOrigen().obtenerTitular().obtenerDni() + 
               " (" + obtenerCuentaOrigen().obtenerCvu() + ")\n" +
               "desc: " + inversion.toString() + "\n" +
               "monto: " + inversion.obtenerMonto() + "\n" +
               "plazo: " + inversion.obtenerPlazoEnDias() + "\n" +
               (fueAprobada() ? "Aprobado" : "Rechazado");
    }
}