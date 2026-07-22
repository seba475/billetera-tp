package ar.edu.ungs.billetera;

public class CuentaPremium extends Cuenta {

    private static final double montoMin = 500000;

    public CuentaPremium(Usuario titular, String cvu, String alias, double depositoInicial) {
        super(titular, cvu, alias);

        if (depositoInicial >= montoMin) {
            acreditar(depositoInicial);
        } else {
            throw new IllegalArgumentException
            ("El depósito inicial debe ser de al menos " + montoMin);
        }
    }

    @Override
    public boolean validarOperacion(double monto, Usuario usuario) {
        return obtenerSaldo() - monto >= 0;
    }

    @Override
    public String toString() {
        return "Premium: " + obtenerAlias() + " (" + obtenerCvu() + ")";
    }
}