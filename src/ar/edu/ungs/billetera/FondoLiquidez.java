package ar.edu.ungs.billetera;

import java.time.LocalDate;

public class FondoLiquidez extends Inversion {
    
    private static final double MONTO_MINIMO = 20000000;
    private static final double TASA_INTERES = 0.08;
    private static final String ACTIVO = "FLE";
    
    public FondoLiquidez(int id, CuentaCorporativa cuenta, double monto, int plazoEnDias) {
        super(id, cuenta, monto, plazoEnDias);
        if (monto < MONTO_MINIMO) {
            throw new IllegalArgumentException("FondoLiquidez requiere un monto mínimo de " + MONTO_MINIMO);
        }
    }
    
    @Override
    public double calcularResultado() {
        double cotizacion = Utilitarios.consultarCotizacion(ACTIVO);
        return obtenerMonto() * TASA_INTERES * obtenerPlazoEnDias() / 365 * cotizacion;
    }
    
    @Override
    public double calcularResultadoHasta(LocalDate fecha) {
        long diasTranscurridos = fecha.toEpochDay() - obtenerFechaInicio().toEpochDay();
        double cotizacion = Utilitarios.consultarCotizacion(ACTIVO);
        double divisasInvertidas = obtenerMonto() / cotizacion;
        double interesesEnDivisas = divisasInvertidas * TASA_INTERES * diasTranscurridos / 365;
        return interesesEnDivisas * cotizacion;
    }
    
    @Override
    public void precancelar() {
        throw new IllegalArgumentException("FondoLiquidez no es precancelable");
    }
    
    @Override
    public String toString() {
        return "FondoLiquidez";
    }
}