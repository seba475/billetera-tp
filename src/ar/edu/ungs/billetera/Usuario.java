package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    
    private String dni;
    private String nombre;
    private String telefono;
    private String email;
    private List<Cuenta> cuentas;
    private double totalInvertido;
    
    public Usuario(String dni, String nombre, String telefono, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.cuentas = new ArrayList<>();
        this.totalInvertido = 0;
    }
    
    public void agregarCuenta(Cuenta cta) {
        if (cta == null) {
            throw new IllegalArgumentException("Cuenta inválida");
        }
        cuentas.add(cta);
    }
    
    public List<Cuenta> consultarCuentas() {
        return new ArrayList<>(cuentas);
    }
    
    public void sumarInvertido(double monto) {
        if (monto < 0) {
            throw new IllegalArgumentException("Monto inválido");
        }
        totalInvertido += monto;
    }
    
    public void restarInvertido(double monto) {
        if (monto < 0) {
            throw new IllegalArgumentException("Monto inválido");
        }
        totalInvertido -= monto;
    }
    
    public double obtenerTotalInvertido() {
        return totalInvertido;
    }
    
    public String obtenerDni() {
        return dni;
    }
    
    public String obtenerNombre() {
        return nombre;
    }
    
    @Override
    public String toString() {
        return "(Usuario: " + nombre + " DNI: " + dni + ")";
    }
}
