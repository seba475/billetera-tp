package ar.edu.ungs.billetera;

public class CuentaPremium extends Cuenta {

    private static final double MONTO_MINIMO = 500000;

    public CuentaPremium(Usuario titular, String cvu, String alias, double depositoInicial) {
        super(titular, cvu, alias);

        if (depositoInicial >= MONTO_MINIMO) {
            acreditar(depositoInicial);
        } else {
            throw new IllegalArgumentException
            ("El depósito inicial debe ser de al menos " + MONTO_MINIMO);
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