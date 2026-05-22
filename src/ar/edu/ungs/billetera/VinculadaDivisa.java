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
    
    public String obtenerDivisa() {
        return divisa;
    }
    
    public double obtenerCotizacionInicial() {
        return cotizacionInicial;
    }
    
    public double obtenerTasaInteres() {
        return tasaInteres;
    }
    
    @Override
    public double calcularResultado() {
        double cotizacion = Utilitarios.consultarCotizacion(divisa);
        double resultado = obtenerMonto() * tasaInteres * obtenerPlazoEnDias() / 365 * cotizacion;
        if (estaPrecancelada()) {
            resultado = resultado / 2;
        }
        return resultado;
    }
    
    @Override
    public double calcularResultadoHasta(LocalDate fecha) {
        long diasTranscurridos = fecha.toEpochDay() - obtenerFechaInicio().toEpochDay();
        double divisasInvertidas = obtenerMonto() / cotizacionInicial;
        double interesesEnDivisas = divisasInvertidas * tasaInteres * diasTranscurridos / 365;
        double cotizacionActual = Utilitarios.consultarCotizacion(divisa);
        return interesesEnDivisas * cotizacionActual;
    }
    
    @Override
    public String toString() {
        return "VinculadaDivisa " + divisa;
    }
}