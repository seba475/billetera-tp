package ar.edu.ungs.billetera;

public class CuentaCorporativa extends Cuenta {

    private Empresa empresa;
    
    public CuentaCorporativa(Usuario titular, String cvu, String alias, Empresa empresa) {
        super(titular, cvu, alias);
        this.empresa = empresa;
    
    }
    @Override
    public boolean validarOperacion(double monto, Usuario usuario) {
        
        return empresa.estaAutorizado(usuario.obtenerDni()) && obtenerSaldo() - monto >= 0;
    }
    @Override
    public String toString() {
        return "Corporativa: " + obtenerAlias() + " (" + obtenerCvu() + ")";
    }

}