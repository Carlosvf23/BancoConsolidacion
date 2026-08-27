import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Persona persona1 = new Persona(
                "Luciano",
                "19.054.257-2",
                LocalDate.of(1995, 3, 7)
        );

        //System.out.println("Edad: " + persona1.getEdad());
        CuentaBancaria cuentaCarlos = new CuentaBancaria("001",persona1);

        //cuentaCarlos.depositar(50000);
        Banco banco = new Banco();

        System.out.println(banco.agregarCuenta(cuentaCarlos));
        System.out.println(banco.agregarCuenta(cuentaCarlos));

        //System.out.println(cuentaCarlos.getSaldo());
        /*CuentaBancaria encontrada = banco.buscarCuenta("001");
        if (encontrada != null) {

            System.out.println(encontrada.getNumeroCuenta());

        } else {

            System.out.println("La cuenta no existe.");
        }*/

        //cuentaCarlos.retirar(100000);
        // asi se tira una variable directamente para ver si es true o false
        // System.out.println(cuentaCarlos.retirar(100000));
        //System.out.println(cuentaCarlos.getSaldo());
        //System.out.println(encontrada.getNumeroCuenta());

    }

}
