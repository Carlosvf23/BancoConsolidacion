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
    public void bloquearCuenta() {
        estado = EstadoCuenta.BLOQUEADA;
    }
    public void activarCuenta() {estado = EstadoCuenta.ACTIVA;
    }

    public boolean depositar(double monto) {
        if (monto <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor a 0");
        }

        saldo += monto;
        return true;
    }

    public boolean retirar(double monto) {
        if (estado != EstadoCuenta.ACTIVA) {
            throw new CuentaBloqueadaException("La cuenta no está activa");
        }


        if (monto <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor a 0");
        }

        if (monto > saldo) {
            throw new SaldoInsuficienteException("La cuenta no tiene saldo suficiente");
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