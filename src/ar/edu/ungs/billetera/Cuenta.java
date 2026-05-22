package ar.edu.ungs.billetera;

    public abstract class Cuenta {

        private Usuario titular;
        private String cvu;
        private String alias;
        private double saldo;
        private int cantidadTransacciones;

        public Cuenta(Usuario titular, String cvu, String alias) {
            this.titular=titular;
            this.cvu = cvu;
            this.alias = alias;
            this.saldo = 0;
            this.cantidadTransacciones = 0;
        }

        public void acreditar(double monto) {
            saldo += monto;
            cantidadTransacciones++;
        }

        public void debitar(double monto) {
            saldo -= monto;
            cantidadTransacciones++;
        }

        public double obtenerSaldo() {
            return saldo;
        }

        public int obtenerCantidadTransacciones() {
            return cantidadTransacciones;
        }
        public Usuario obtenerTitular() {
            return titular;
        }

        public String obtenerCvu() {
            return cvu;
        }

        public String obtenerAlias() {
            return alias;
        }

        public abstract boolean validarOperacion(double monto, Usuario usuario);

        @Override
        public abstract String toString();
}