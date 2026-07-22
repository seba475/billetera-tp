package ar.edu.ungs.billetera;

import java.time.LocalDate;

public class RentaFija extends Inversion {
    
    private static final double tasaInteres = 0.20;
    
    public RentaFija(int id, Cuenta cuentaOrigen, double monto, int plazoEnDias) {
        super(id, cuentaOrigen, monto, plazoEnDias);
    }
    
    @Override
    public double calcularResultadoHasta(LocalDate fecha) {
        long diasTranscurridos = fecha.toEpochDay() - obtenerFechaInicio().toEpochDay();
        return obtenerMonto() * (tasaInteres / 365) * diasTranscurridos;
    }
    
    @Override
    public double calcularDevolucionPorPrecancelacion(LocalDate fecha) {
        double intereses = calcularResultadoHasta(fecha);
        return obtenerMonto() + (intereses / 2);
    }
    
    @Override
    public String toString() {
        return "RentaFija";
    }
}