package cl.carlos.banco.model;

import cl.carlos.banco.exception.CuentaBloqueadaException;
import cl.carlos.banco.exception.MontoInvalidoException;
import cl.carlos.banco.exception.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.util.Objects;
public abstract class CuentaBancaria {

    private final String numeroCuenta;
    private Persona titular;
    private BigDecimal saldo;
    private EstadoCuenta estado;

    private static int cantidadTotalCuentasCreadas = 0;

    public CuentaBancaria(String numeroCuenta, Persona titular) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = BigDecimal.ZERO;
        this.estado = EstadoCuenta.ACTIVA;

        cantidadTotalCuentasCreadas++;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public Persona getTitular() {
        return titular;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public static int getCantidadTotalCuentasCreadas() {
        return cantidadTotalCuentasCreadas;
    }

    public void bloquearCuenta() {
        estado = EstadoCuenta.BLOQUEADA;
    }

    public void activarCuenta() {
        estado = EstadoCuenta.ACTIVA;
    }
    public void cerrarCuenta() {estado = EstadoCuenta.CERRADA;
    }

    public boolean depositar(BigDecimal monto) {

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MontoInvalidoException(
                    "El monto debe ser mayor a 0"
            );
        }

        saldo = saldo.add(monto);
        return true;
    }

    public boolean retirar(BigDecimal monto) {

        if (estado != EstadoCuenta.ACTIVA) {
            throw new CuentaBloqueadaException(
                    "La cuenta no está activa"
            );
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MontoInvalidoException(
                    "El monto debe ser mayor a 0"
            );
        }

        if (monto.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException(
                    "La cuenta no tiene saldo suficiente"
            );
        }

        saldo = saldo.subtract(monto);
        return true;
    }


    public abstract BigDecimal calcularCostoMantencion();
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof CuentaBancaria cuenta)) {
            return false;
        }

        return Objects.equals(numeroCuenta, cuenta.numeroCuenta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroCuenta);
    }
    @Override
    public String toString() {
        return "cl.carlos.banco.model.CuentaBancaria{" +
                "numeroCuenta='" + numeroCuenta + '\'' +
                ", titular=" + titular +
                ", saldo=" + saldo +
                ", estado=" + estado +
                '}';
    }
}