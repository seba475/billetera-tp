package ar.edu.ungs.billetera;

public class CuentaRegular extends Cuenta {
    
    private static final double saldoMax = 5000000;
    
    public CuentaRegular(Usuario titular, String cvu, String alias) {
        super(titular, cvu, alias);
    }
    
    @Override
    public void acreditar(double monto) {
        if (obtenerSaldo() + monto > saldoMax) {
            throw new IllegalStateException("Excede el saldo máximo de la cuenta regular");
        }
        super.acreditar(monto);
    }
    
    @Override
    public boolean validarOperacion(double monto, Usuario usuario) {
        return obtenerSaldo() - monto >= 0;
    }
    
    @Override
    public String toString() {
        return "Regular: " + obtenerAlias() + " (" + obtenerCvu() + ")";
    }
}