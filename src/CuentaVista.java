public class CuentaVista extends CuentaBancaria {
    public CuentaVista(String numeroCuenta, Persona titular) {
        super(numeroCuenta, titular);
    }
    @Override
    public double calcularCostoMantencion() {
        return 2500;

    }

}
