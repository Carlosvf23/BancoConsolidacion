import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Persona persona1 = new Persona(
                "Carlos",
                "12345678-9",
                LocalDate.of(1995, 3, 7)
        );

        Banco banco = new Banco();

        System.out.println(persona1);

        System.out.println("Edad: " + persona1.getEdad());

       // CuentaVista cuentaVista = new CuentaVista("003",persona1);
        CuentaBancaria cuentaVista = new CuentaVista("003",persona1);

        System.out.println(cuentaVista.calcularCostoMantencion());
        System.out.println(CuentaBancaria.getCantidadTotalCuentasCreadas());


        CuentaCorriente cuentaCorriente = new CuentaCorriente("001",persona1,500000
        );
        cuentaCorriente.depositar(100000);

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

        try {
            cuentaCorriente.retirar(5000000);

        } catch (CuentaBloqueadaException e) {
            System.out.println("Error " + e.getMessage());

        } catch (MontoInvalidoException e) {
            System.out.println("Error " + e.getMessage());

        } catch (SaldoInsuficienteException e) {
            System.out.println("Error " + e.getMessage());
        }

    }
}