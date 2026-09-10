import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CuentaCorrienteTest {

    private CuentaCorriente origen;
    private CuentaBancaria destino;

    @BeforeEach
    void prepararCuentas() {

        Persona persona = new Persona(
                "Carlos",
                "12345678-9",
                LocalDate.of(1995, 3, 7)
        );

        origen = new CuentaCorriente(
                "001",
                persona,
                new BigDecimal("500000")
        );

        destino = new CuentaVista(
                "003",
                persona
        );

        origen.depositar(
                new BigDecimal("100000")
        );
    }

    @Test
    void transferirDeberiaMoverDineroEntreCuentas() {

        boolean resultado = origen.transferir(
                destino,
                new BigDecimal("30000")
        );

        assertTrue(resultado);

        assertEquals(
                new BigDecimal("70000"),
                origen.getSaldo()
        );

        assertEquals(
                new BigDecimal("30000"),
                destino.getSaldo()
        );
    }
}