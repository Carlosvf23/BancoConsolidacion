import java.math.BigDecimal;

public class CuentaCorriente extends CuentaBancaria implements Transferible {

    private BigDecimal lineaCredito;

    public CuentaCorriente(
            String numeroCuenta,
            Persona titular,
            BigDecimal lineaCredito
    ) {
        super(numeroCuenta, titular);
        this.lineaCredito = lineaCredito;
    }

    public BigDecimal getLineaCredito() {
        return lineaCredito;
    }

    @Override
    public BigDecimal calcularCostoMantencion() {
        return new BigDecimal("5000");
    }

    @Override
    public boolean transferir(
            CuentaBancaria destino,
            BigDecimal monto
    ) {

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