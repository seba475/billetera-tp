package ar.edu.ungs.billetera;

import java.time.LocalDate;

public class FondoLiquidez extends Inversion {
    
    private static final double montoMin = 20000000;
    private static final double tasaInteres = 0.08;
    private static final String activo = "FLE";
    private double cotizacionInicial;
    
    public FondoLiquidez(int id, CuentaCorporativa cuenta, double monto, int plazoEnDias) {
        super(id, cuenta, monto, plazoEnDias);
        if (monto < montoMin) {
            throw new IllegalArgumentException("FondoLiquidez requiere un monto mínimo de " + montoMin);
        }
        this.cotizacionInicial = Utilitarios.consultarCotizacion(activo);
    }
    
    @Override
    public double calcularResultadoHasta(LocalDate fecha) {
        // Vemos cuántos días pasaron desde que se hizo la inversión hasta ahora
        long diasTranscurridos = fecha.toEpochDay() - obtenerFechaInicio().toEpochDay();
        
        // Calculamos cuántas unidades del activo compramos con los pesos invertidos
        double divisasInvertidas = obtenerMonto() / cotizacionInicial;
        
        // Sacamos los intereses que generaron esas unidades en estos días
        double interesesEnDivisas = divisasInvertidas * (tasaInteres / 365) * diasTranscurridos;
        
        // Convertimos los intereses a pesos con la cotización actual
        double cotizacionActual = Utilitarios.consultarCotizacion(activo);
        return interesesEnDivisas * cotizacionActual;
    }
    
    @Override
    public void precancelar() {
        throw new IllegalArgumentException("FondoLiquidez no es precancelable");
    }
    
    @Override
    public double calcularDevolucionPorPrecancelacion(LocalDate fecha) {
        throw new IllegalArgumentException("FondoLiquidez no es precancelable");
    }
    
    @Override
    public String toString() {
        return "FondoLiquidez";
    }
}