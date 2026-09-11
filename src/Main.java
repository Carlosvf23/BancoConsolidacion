import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.SQLException;
public class Main {

    public static void main(String[] args) {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            System.out.println("Conexión a PostgreSQL exitosa");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
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
        PersonaDAO personaDAO = new PersonaDAO();

        for (Persona persona : personaDAO.listarPersonas()) {
            System.out.println("Desde PostgreSQL: " + persona);
        }
        personaDAO.buscarPorRut("12345678-9")
                .ifPresent(persona ->
                        System.out.println("Encontrada: " + persona)
                );
        System.out.println(
                personaDAO.buscarPorRut("99999999-9")
        );
        Persona persona3 = new Persona(
                "Mariela",
                "11111118-1",
                LocalDate.of(1998, 5, 15)
        );

        try {

            boolean guardada = personaDAO.guardar(persona3);

            System.out.println(
                    "Persona guardada: " + guardada
            );

        } catch (PersonaDuplicadaException e) {

            System.out.println(
                    "No se pudo guardar: " + e.getMessage()
            );
        }
        personaDAO.buscarPorRut("11111111-1")
                .ifPresent(persona ->
                        System.out.println(
                                "Desde BD: " + persona
                        )
                );
        Persona personaActualizada = new Persona(
                "Andrea Actualizada",
                "11111118-1",
                LocalDate.of(1998, 5, 15)
        );

        boolean actualizada =
                personaDAO.actualizar(personaActualizada);

        System.out.println(
                "Persona actualizada: " + actualizada
        );
        personaDAO.buscarPorRut("11111118-1")
                .ifPresent(System.out::println);

        try {

            personaDAO.eliminarPorRut("12345678-9");

        } catch (PersonaConCuentasException e) {

            System.out.println("Error: " + e.getMessage());
        }
    }


}