package ar.edu.ungs.billetera;

import java.time.LocalDate;

public abstract class Inversion {
    
    private int id;
    private LocalDate fechaInicio;
    private Cuenta cuentaOrigen;
    private double monto;
    private int plazoEnDias;
    private boolean precancelada;
    
    public Inversion(int id, Cuenta cuentaOrigen, double monto, int plazoEnDias) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        this.id = id;
        this.cuentaOrigen = cuentaOrigen;
        this.monto = monto;
        this.plazoEnDias = plazoEnDias;
        this.precancelada = false;
        this.fechaInicio = Utilitarios.hoy();
    }
    
    public int obtenerId() {
        return id;
    }
    
    public Cuenta obtenerCuentaOrigen() {
        return cuentaOrigen;
    }
    
    public LocalDate obtenerFechaInicio() {
        return fechaInicio;
    }
    
    public double obtenerMonto() {
        return monto;
    }
    
    public int obtenerPlazoEnDias() {
        return plazoEnDias;
    }
    
    public boolean estaPrecancelada() {
        return precancelada;
    }
    
    public void precancelar() {	
        this.precancelada = true;
    }
    
    public abstract double calcularResultadoHasta(LocalDate fecha);
    
    public abstract double calcularDevolucionPorPrecancelacion(LocalDate fecha);
    
    @Override
    public abstract String toString();
}