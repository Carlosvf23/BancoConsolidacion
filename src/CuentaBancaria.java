public abstract class CuentaBancaria {

    private final String numeroCuenta;
    private Persona titular;
    private double saldo;
    private EstadoCuenta estado;
    private static int cantidadTotalCuentasCreadas = 0;

    public CuentaBancaria(String numeroCuenta, Persona titular) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = 0;
        this.estado = EstadoCuenta.ACTIVA;

        cantidadTotalCuentasCreadas++;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public Persona getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }
    public EstadoCuenta getEstado(){
        return estado;
    }

    public static int getCantidadTotalCuentasCreadas() {
        return cantidadTotalCuentasCreadas;
    }


    public boolean depositar(double monto) {
        if (monto <= 0) {
            return false;
        }

        saldo += monto;
        return true;
    }

    public boolean retirar(double monto) {
        if (monto <= 0 || monto > saldo) {
            return false;
        }

        saldo -= monto;
        return true;
    }

    public abstract double calcularCostoMantencion();

    @Override
    public String toString() {
        return "CuentaBancaria{" +
                "numeroCuenta='" + numeroCuenta + '\'' +
                ", titular=" + titular +
                ", saldo=" + saldo +
                '}';
    }
}