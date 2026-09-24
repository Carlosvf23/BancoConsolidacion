package cl.carlos.banco.model;

import java.math.BigDecimal;

public interface Transferible {

    boolean transferir(
            CuentaBancaria destino,
            BigDecimal monto
    );
}