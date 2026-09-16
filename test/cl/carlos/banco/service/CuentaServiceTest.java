package cl.carlos.banco.service;

import cl.carlos.banco.dao.CuentaDAO;
import cl.carlos.banco.exception.MontoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CuentaServiceTest {

    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {

        CuentaDAO cuentaDAO =
                new CuentaDAO();

        cuentaService =
                new CuentaService(cuentaDAO);
    }

    @Test
    void transferirConMontoNegativoDebeLanzarExcepcion() {

        assertThrows(
                MontoInvalidoException.class,
                () -> cuentaService.transferir(
                        "001",
                        "003",
                        new BigDecimal("-5000")
                )
        );
    }

    @Test
    void transferirConMontoCeroDebeLanzarExcepcion() {

        assertThrows(
                MontoInvalidoException.class,
                () -> cuentaService.transferir(
                        "001",
                        "003",
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void transferirConMontoNullDebeLanzarExcepcion() {

        assertThrows(
                MontoInvalidoException.class,
                () -> cuentaService.transferir(
                        "001",
                        "003",
                        null
                )
        );
    }

    @Test
    void transferirALaMismaCuentaDebeLanzarExcepcion() {

        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.transferir(
                        "001",
                        "001",
                        new BigDecimal("5000")
                )
        );
    }
}