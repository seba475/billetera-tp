package ar.edu.ungs.billetera;

import java.time.LocalDate;

public abstract class Actividad {
    
    private LocalDate fecha;
    private Cuenta cuentaOrigen;
    private boolean aprobada;
    
    public Actividad(Cuenta cuentaOrigen, boolean aprobada) {
        this.cuentaOrigen = cuentaOrigen;
        this.aprobada = aprobada;
        this.fecha = Utilitarios.hoy();
    }
    
    public boolean involucraCuenta(Cuenta cuenta) {
        return cuentaOrigen == cuenta;
    }
    
    public boolean involucraUsuario(Usuario usuario) {
        return cuentaOrigen.obtenerTitular() == usuario;
    }
    
    public Cuenta obtenerCuentaOrigen() {
        return cuentaOrigen;
    }
    
    public boolean fueAprobada() {
        return aprobada;
    }
    
    public LocalDate obtenerFecha() {
        return fecha;
    }
    
    public abstract String toString();
}