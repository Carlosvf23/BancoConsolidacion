import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Persona persona1 = new Persona(
                "Carlos",
                "12345678-9",
                LocalDate.of(1995, 3, 7)
        );

        Persona persona2 = new Persona(
                "Felipe",
                "12345678-4",
                LocalDate.of(1995, 3, 7)
        );

        Banco banco = new Banco();

        CuentaBancaria cuentaVista =
                new CuentaVista("003", persona1);

        CuentaBancaria cuentaVista2 =
                new CuentaVista("004", persona2);

        CuentaCorriente cuentaCorriente =
                new CuentaCorriente(
                        "001",
                        persona1,
                        new BigDecimal("500000")
                );

        banco.agregarCuenta(cuentaVista);
        banco.agregarCuenta(cuentaVista2);
        banco.agregarCuenta(cuentaCorriente);

        cuentaCorriente.depositar(
                new BigDecimal("100000")
        );

        cuentaCorriente.transferir(
                cuentaVista,
                new BigDecimal("30000")
        );

        System.out.println(
                "Saldo corriente: "
                        + cuentaCorriente.getSaldo()
        );

        System.out.println(
                "Saldo vista: "
                        + cuentaVista.getSaldo()
        );

        System.out.println(
                "Cuentas activas: "
                        + banco.contarCuentasPorEstado(
                        EstadoCuenta.ACTIVA)
        );
    }
}