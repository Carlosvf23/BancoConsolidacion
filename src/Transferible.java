import java.math.BigDecimal;

public interface Transferible {

    boolean transferir(
            CuentaBancaria destino,
            BigDecimal monto
    );
}