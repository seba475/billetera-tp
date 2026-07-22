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
    
    public List<String> listarCuentas() {
        List<String> resultado = new ArrayList<>();
        for (Cuenta c : cuentas) {
            resultado.add(c.toString());
        }
        return resultado;
    }
    
    public String buscarCvuPorAlias(String alias) {
        for (Cuenta c : cuentas) {
            if (c.obtenerAlias().equals(alias)) {
                return c.obtenerCvu();
            }
        }
        return null;
    }
    
    public void aportarCuentas(List<Cuenta> destino) {
        destino.addAll(cuentas);
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
    
    public void debitarDe(Cuenta cuenta, double monto) {
        cuenta.debitar(monto);
    }

    public void acreditarEn(Cuenta cuenta, double monto) {
        cuenta.acreditar(monto);
    }

    public void invertirEn(Cuenta cuenta, double monto) {
        cuenta.debitar(monto);
        sumarInvertido(monto);
    }

    public void recibirDevolucion(Cuenta cuenta, double montoDevuelto, double montoInvertido) {
        cuenta.acreditar(montoDevuelto);
        restarInvertido(montoInvertido);
    }
    
    public double obtenerTotalInvertido() {
        return totalInvertido;
    }
    
    public String obtenerDni() {
        return dni;
    }
    
    public String detalleConCuentas() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        for (Cuenta c : cuentas) {
            sb.append("- ").append(c.toString()).append(" - Saldo: $").append(c.obtenerSaldo()).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "Usuario: " + nombre + " DNI: " + dni;
    }
}
