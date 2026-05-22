package ar.edu.ungs.billetera;

import java.time.LocalDateTime;

public abstract class Actividad {
    
    private LocalDateTime fechaHora;
    private Cuenta cuentaOrigen;
    private boolean aprobada;
    
    public Actividad(Cuenta cuentaOrigen, boolean aprobada) {
        this.cuentaOrigen = cuentaOrigen;
        this.aprobada = aprobada;
        this.fechaHora = LocalDateTime.now();
    }
    
    public Cuenta obtenerCuentaOrigen() {
        return cuentaOrigen;
    }
    
    public boolean fueAprobada() {
        return aprobada;
    }
    
    public LocalDateTime obtenerFechaHora() {
        return fechaHora;
    }
    
    public abstract String toString();
}