package cl.carlos.banco.model;

import cl.carlos.banco.exception.CuentaNoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BancoTest {

    private Banco banco;
    private CuentaBancaria cuenta;

    @BeforeEach
    void prepararBanco() {

        Persona persona = new Persona(
                "Carlos",
                "12345678-9",
                LocalDate.of(1995, 3, 7)
        );

        banco = new Banco();

        cuenta = new CuentaVista(
                "001",
                persona
        );

        banco.agregarCuenta(cuenta);
    }

    @Test
    void buscarCuentaExistenteDeberiaEncontrarla() {

        assertTrue(
                banco.buscarCuenta("001").isPresent()
        );
    }

    @Test
    void buscarCuentaInexistenteDeberiaDevolverOptionalVacio() {

        assertTrue(
                banco.buscarCuenta("999").isEmpty()
        );
    }

    @Test
    void agregarCuentaDuplicadaDeberiaDevolverFalse() {

        boolean resultado =
                banco.agregarCuenta(cuenta);

        assertFalse(resultado);
    }

    @Test
    void retirarDeCuentaInexistenteDeberiaLanzarExcepcion() {

        assertThrows(
                CuentaNoEncontradaException.class,
                () -> banco.retirar(
                        "999",
                        new BigDecimal("10000")
                )
        );
    }
}