package ar.edu.ungs.billetera;

import java.time.LocalDate;

public class RentaFija extends Inversion {
    
    private static final double TASA_INTERES = 0.20;
    
    public RentaFija(int id, Cuenta cuentaOrigen, double monto, int plazoEnDias) {
        super(id, cuentaOrigen, monto, plazoEnDias);
    }
    
    @Override
    public double calcularResultado() {
        double resultado = obtenerMonto() * TASA_INTERES * obtenerPlazoEnDias() / 365;
        if (estaPrecancelada()) {
            resultado = resultado / 2;
        }
        return resultado;
    }
    
    @Override
    public double calcularResultadoHasta(LocalDate fecha) {
        long diasTranscurridos = fecha.toEpochDay() - obtenerFechaInicio().toEpochDay();
        return obtenerMonto() * TASA_INTERES * diasTranscurridos / 365;
    }
    
    @Override
    public String toString() {
        return "RentaFija";
    }
}