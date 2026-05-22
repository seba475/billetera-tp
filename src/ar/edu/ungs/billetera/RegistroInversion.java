package ar.edu.ungs.billetera;

public class RegistroInversion extends Actividad {
    
    private Inversion inversion;
    private String tipoEvento;
    
    public RegistroInversion(Cuenta cuentaOrigen, Inversion inversion, String tipoEvento, boolean aprobada) {
        super(cuentaOrigen, aprobada);
        this.inversion = inversion;
        this.tipoEvento = tipoEvento;
    }
    
    @Override
    public String toString() {
        return "fecha: " + obtenerFechaHora() + "\n" +
               "origen: " + obtenerCuentaOrigen().obtenerTitular().obtenerDni() + 
               " (" + obtenerCuentaOrigen().obtenerCvu() + ")\n" +
               "desc: " + inversion.toString() + "\n" +
               "monto: " + inversion.obtenerMonto() + "\n" +
               "plazo: " + inversion.obtenerPlazoEnDias() + "\n" +
               (fueAprobada() ? "Aprobado" : "Rechazado");
    }
}