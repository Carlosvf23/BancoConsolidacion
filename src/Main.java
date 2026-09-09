import jdk.swing.interop.SwingInterOpUtils;

import java.time.LocalDate;
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

       // CuentaVista cuentaVista = new CuentaVista("003",persona1);
        CuentaBancaria cuentaVista = new CuentaVista("003",persona1);
        CuentaBancaria cuentaVista1 = new CuentaVista("004",persona2);
        banco.agregarCuenta(cuentaVista);
        banco.agregarCuenta(cuentaVista1);
        System.out.println(cuentaVista.calcularCostoMantencion());



        CuentaCorriente cuentaCorriente = new CuentaCorriente("001",persona1,500000
        );
        cuentaCorriente.depositar(100000);
        banco.agregarCuenta(cuentaCorriente);
        cuentaCorriente.transferir(cuentaVista,30000);

        cuentaCorriente.getSaldo();
        cuentaVista.getSaldo();

        System.out.println("Saldo cuenta corriente: " + cuentaCorriente.getSaldo());
        System.out.println("Saldo cuenta vista: " + cuentaVista.getSaldo());
        System.out.println(cuentaCorriente.getEstado());

        cuentaCorriente.bloquearCuenta();


        System.out.println("Estado: " + cuentaCorriente.getEstado());

        try {

            boolean resultado = cuentaCorriente.transferir(cuentaVista, 30000);

            System.out.println("Transferencia: " + resultado);

        } catch (CuentaBloqueadaException e) {

            System.out.println("Error: " + e.getMessage());
        }



        try {

            cuentaCorriente.retirar(10000);

            System.out.println("Retiro realizado");

        } catch (CuentaBloqueadaException e) {

            System.out.println("Error: " + e.getMessage());

        }


        /*try {
            cuentaCorriente.retirar(-5000);
        } catch (SaldoInsuficienteException e) {
            System.out.println("Error:"+ e.getMessage());

        }*/

        /*try {
            cuentaCorriente.retirar(5000000);

        } catch (CuentaBloqueadaException e) {
            System.out.println("Error " + e.getMessage());

        } catch (MontoInvalidoException e) {
            System.out.println("Error " + e.getMessage());

        } catch (SaldoInsuficienteException e) {
            System.out.println("Error " + e.getMessage());
        }*/
        try {
            cuentaCorriente.depositar(-2000);
        }catch (MontoInvalidoException e){
            System.out.println("Error "+ e.getMessage());
        }

        for (CuentaBancaria cuenta :
                banco.obtenerCuentasPorEstadoStream(EstadoCuenta.ACTIVA)) {

            System.out.println(cuenta);
        }
        for (CuentaBancaria cuenta : banco.obtenerCuentasConSaldoMayorA(10000000)) {
            System.out.println(cuenta);
        }
        System.out.println(
                "Cuentas activas: " +
                        banco.contarCuentasPorEstado(EstadoCuenta.BLOQUEADA)
        );
        System.out.println(banco.existeCuentaConSaldoMayorA(1000)
        );
        System.out.println(
                banco.existeCuentaConSaldoMayorA(1000000)
        );
        for (String numero : banco.obtenerNumerosDeCuenta()) {
            System.out.println("Número: " + numero);
        }
        for (String numero : banco.obtenerNumerosCuentasActivas()) {
            System.out.println("Cuenta activa: " + numero);
        }
        Optional<CuentaBancaria> resultado = banco.obtenerPrimeraCuentaActiva();

        if (resultado.isPresent()) {
            System.out.println("Primera activa: " + resultado.get());
        }
        banco.obtenerPrimeraCuentaActiva()
                .ifPresent(cuenta -> System.out.println("Primera activa: " + cuenta));

        System.out.println(banco.buscarCuenta("003"));
        System.out.println(banco.buscarCuenta("999"));

        try {
            banco.retirar("999", 10000);
        } catch (CuentaNoEncontradaException e) {
            System.out.println("Error: " + e.getMessage());
        }


    }

}