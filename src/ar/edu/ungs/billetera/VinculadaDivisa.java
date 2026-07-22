package ar.edu.ungs.billetera;

import java.time.LocalDate;

public class VinculadaDivisa extends Inversion {
    
    private String divisa;
    private double tasaInteres;
    private double cotizacionInicial;
    
    public VinculadaDivisa(int id, Cuenta cuentaOrigen, double monto, int plazoEnDias, String divisa, double tasaInteres) {
        super(id, cuentaOrigen, monto, plazoEnDias);
        this.divisa = divisa;
        this.tasaInteres = tasaInteres;
        this.cotizacionInicial = Utilitarios.consultarCotizacion(divisa);
    }
    
    @Override
    public double calcularResultadoHasta(LocalDate fecha) {
        //Vemos cuántos días pasaron desde que se hizo la inversión hasta ahora
        long diasTranscurridos = fecha.toEpochDay() - obtenerFechaInicio().toEpochDay();
        
        // Calculamos cuántos divisas compramos con los pesos invertidos
        // Usamos la cotización del día que invertimos
        double divisasInvertidas = obtenerMonto() / cotizacionInicial;
        
        // Sacamos los intereses que generaron esas divisas en estos días
        double interesesEnDivisas = divisasInvertidas * (tasaInteres / 365) * diasTranscurridos;
        
        // Convertimos esos intereses a pesos, usando la cotización de hoy
        double cotizacionActual = Utilitarios.consultarCotizacion(divisa);
        return interesesEnDivisas * cotizacionActual;
    }
    
    @Override
    public double calcularDevolucionPorPrecancelacion(LocalDate fecha) {
        double divisasInvertidas = obtenerMonto() / cotizacionInicial;
        double intereses = calcularResultadoHasta(fecha) / 2;
        double cotizacionActual = Utilitarios.consultarCotizacion(divisa);
        return (divisasInvertidas * cotizacionActual) + intereses;
    }
    
    @Override
    public String toString() {
        return "VinculadaDivisa " + divisa;
    }
}