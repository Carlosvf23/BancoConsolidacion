public class CuentaCorriente extends CuentaBancaria implements Transferible {

    private double lineaCredito;

    public CuentaCorriente(
            String numeroCuenta,
            Persona titular,
            double lineaCredito
    ) {
        super(numeroCuenta, titular);
        this.lineaCredito = lineaCredito;

    }

    @Override
    public double calcularCostoMantencion() {
        return 5000;
    }

    @Override
    public boolean transferir(CuentaBancaria destino, double monto) {

        if (!retirar(monto)) {
            return false;
        }

        if (!destino.depositar(monto)) {
            depositar(monto);
            return false;
        }

        return true;
    }
}