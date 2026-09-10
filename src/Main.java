import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        System.out.println(persona1);
        System.out.println("Edad: " + persona1.getEdad());

        // Crear cuentas vista
        CuentaBancaria cuentaVista =
                new CuentaVista("003", persona1);

        CuentaBancaria cuentaVista1 =
                new CuentaVista("004", persona2);

        banco.agregarCuenta(cuentaVista);
        banco.agregarCuenta(cuentaVista1);

        System.out.println(
                "Costo mantención Cuenta Vista: "
                        + cuentaVista.calcularCostoMantencion()
        );

        // Crear cuenta corriente
        CuentaCorriente cuentaCorriente =
                new CuentaCorriente(
                        "001",
                        persona1,
                        new BigDecimal("500000")
                );

        // Depositar dinero
        cuentaCorriente.depositar(
                new BigDecimal("100000")
        );

        banco.agregarCuenta(cuentaCorriente);

        // Transferencia correcta
        cuentaCorriente.transferir(
                cuentaVista,
                new BigDecimal("30000")
        );

        System.out.println(
                "Saldo cuenta corriente: "
                        + cuentaCorriente.getSaldo()
        );

        System.out.println(
                "Saldo cuenta vista: "
                        + cuentaVista.getSaldo()
        );

        System.out.println(
                "Estado inicial: "
                        + cuentaCorriente.getEstado()
        );

        // Bloquear cuenta
        cuentaCorriente.bloquearCuenta();

        System.out.println(
                "Estado: "
                        + cuentaCorriente.getEstado()
        );

        // PRUEBA: transferencia con cuenta bloqueada
        try {

            boolean resultado = cuentaCorriente.transferir(
                    cuentaVista,
                    new BigDecimal("30000")
            );

            System.out.println(
                    "Transferencia: " + resultado
            );

        } catch (CuentaBloqueadaException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }

        // PRUEBA: retiro con cuenta bloqueada
        try {

            cuentaCorriente.retirar(
                    new BigDecimal("3000")
            );

            System.out.println("Retiro realizado");

        } catch (CuentaBloqueadaException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }

        // Volvemos a activar la cuenta
        cuentaCorriente.activarCuenta();

        // PRUEBA: monto inválido
        try {

            cuentaCorriente.retirar(
                    new BigDecimal("-5000")
            );

        } catch (MontoInvalidoException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }

        // PRUEBA: saldo insuficiente
        try {

            cuentaCorriente.retirar(
                    new BigDecimal("5000000")
            );

        } catch (MontoInvalidoException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );

        } catch (SaldoInsuficienteException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }

        // PRUEBA: depósito inválido
        try {

            cuentaCorriente.depositar(
                    new BigDecimal("-2000")
            );

        } catch (MontoInvalidoException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }

        // STREAM - cuentas activas
        for (CuentaBancaria cuenta :
                banco.obtenerCuentasPorEstadoStream(
                        EstadoCuenta.ACTIVA)) {

            System.out.println(
                    "Cuenta activa: " + cuenta
            );
        }

        // STREAM - cuentas con saldo mayor a $10.000
        for (CuentaBancaria cuenta :
                banco.obtenerCuentasConSaldoMayorA(
                        new BigDecimal("10000"))) {

            System.out.println(
                    "Saldo mayor a 10000: " + cuenta
            );
        }

        // COUNT
        System.out.println(
                "Cuentas bloqueadas: "
                        + banco.contarCuentasPorEstado(
                        EstadoCuenta.BLOQUEADA)
        );

        // ANYMATCH
        System.out.println(
                "¿Existe cuenta con saldo mayor a 1000? "
                        + banco.existeCuentaConSaldoMayorA(
                        new BigDecimal("1000"))
        );

        System.out.println(
                "¿Existe cuenta con saldo mayor a 1000000? "
                        + banco.existeCuentaConSaldoMayorA(
                        new BigDecimal("1000000"))
        );

        // MAP - obtener números de cuenta
        for (String numero :
                banco.obtenerNumerosDeCuenta()) {

            System.out.println(
                    "Número: " + numero
            );
        }

        // FILTER + MAP
        for (String numero :
                banco.obtenerNumerosCuentasActivas()) {

            System.out.println(
                    "Número de cuenta activa: " + numero
            );
        }

        // OPTIONAL - forma tradicional
        Optional<CuentaBancaria> resultado =
                banco.obtenerPrimeraCuentaActiva();

        if (resultado.isPresent()) {

            System.out.println(
                    "Primera activa: "
                            + resultado.get()
            );
        }

        // OPTIONAL - forma moderna con ifPresent
        banco.obtenerPrimeraCuentaActiva()
                .ifPresent(cuenta ->
                        System.out.println(
                                "Primera activa con ifPresent: "
                                        + cuenta
                        )
                );

        // Buscar cuenta existente
        System.out.println(
                banco.buscarCuenta("003")
        );

        // Buscar cuenta inexistente
        System.out.println(
                banco.buscarCuenta("999")
        );

        // CUENTA NO ENCONTRADA
        try {

            banco.retirar(
                    "999",
                    new BigDecimal("10000")
            );

        } catch (CuentaNoEncontradaException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }

        // MAP DE PRÁCTICA
        Map<String, CuentaBancaria> mapaCuentas =
                new HashMap<>();

        mapaCuentas.put(
                "001",
                cuentaCorriente
        );

        mapaCuentas.put(
                "003",
                cuentaVista
        );

        CuentaBancaria encontrada =
                mapaCuentas.get("003");

        System.out.println(
                "Cuenta encontrada en Map: "
                        + encontrada
        );

        // CONTADOR STATIC
        System.out.println(
                "Total cuentas creadas: "
                        + CuentaBancaria
                        .getCantidadTotalCuentasCreadas()
        );
    }
}