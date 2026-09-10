import java.math.BigDecimal;

public class CuentaVista extends CuentaBancaria {

    public CuentaVista(String numeroCuenta, Persona titular) {
        super(numeroCuenta, titular);
    }

    @Override
    public BigDecimal calcularCostoMantencion() {
        return new BigDecimal("2500");
    }
}