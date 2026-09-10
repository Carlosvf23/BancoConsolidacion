import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaBancariaTest {

    private CuentaBancaria cuenta;

    @BeforeEach
    void prepararCuenta() {

        Persona persona = new Persona(
                "Carlos",
                "12345678-9",
                LocalDate.of(1995, 3, 7)
        );

        cuenta = new CuentaVista("001", persona);
    }

    @Test
    void depositarDeberiaAumentarElSaldo() {

        cuenta.depositar(new BigDecimal("10000"));

        assertEquals(
                new BigDecimal("10000"),
                cuenta.getSaldo()
        );
    }

    @Test
    void retirarMontoMayorAlSaldoDeberiaLanzarExcepcion() {

        cuenta.depositar(new BigDecimal("10000"));

        assertThrows(
                SaldoInsuficienteException.class,
                () -> cuenta.retirar(new BigDecimal("50000"))
        );

    }
    @Test
    void retirarConCuentaBloqueadaDeberiaLanzarExcepcion() {

        cuenta.bloquearCuenta();

        assertThrows(
                CuentaBloqueadaException.class,
                () -> cuenta.retirar(new BigDecimal("1000"))
        );
    }
    @Test
    void depositarMontoNegativoDeberiaLanzarExcepcion() {

        assertThrows(
                MontoInvalidoException.class,
                () -> cuenta.depositar(new BigDecimal("-1000"))
        );
    }

}