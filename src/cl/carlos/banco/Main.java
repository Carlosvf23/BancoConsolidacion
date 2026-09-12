package cl.carlos.banco;

import cl.carlos.banco.dao.CuentaDAO;
import cl.carlos.banco.dao.PersonaDAO;
import cl.carlos.banco.database.ConexionBD;
import cl.carlos.banco.exception.CuentaDuplicadaException;
import cl.carlos.banco.exception.CuentaNoEncontradaException;
import cl.carlos.banco.exception.PersonaConCuentasException;
import cl.carlos.banco.exception.PersonaDuplicadaException;
import cl.carlos.banco.model.*;

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
                    "cl.carlos.banco.model.Persona guardada: " + guardada
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
                "cl.carlos.banco.model.Persona actualizada: " + actualizada
        );
        personaDAO.buscarPorRut("11111118-1")
                .ifPresent(System.out::println);

        try {

            personaDAO.eliminarPorRut("12345678-9");

        } catch (PersonaConCuentasException e) {

            System.out.println("Error: " + e.getMessage());
        }
        CuentaDAO cuentaDAO = new CuentaDAO();

        cuentaDAO.buscarPorNumero("001")
                .ifPresent(cuenta ->
                        System.out.println(
                                "Cuenta desde PostgreSQL: " + cuenta
                        )
                );
        System.out.println(
                cuentaDAO.buscarPorNumero("999")
        );
        CuentaBancaria nuevaCuenta =
                new CuentaVista(
                        "010",
                        persona1
                );
        CuentaCorriente cuentaCredito = new CuentaCorriente(
                "011",
                persona1,
                new BigDecimal("500000")
        );

        try {

            cuentaDAO.guardar(cuentaCredito);

            System.out.println(
                    "Cuenta corriente guardada correctamente"
            );

        } catch (CuentaDuplicadaException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
        try {

            boolean cuentaGuardada =
                    cuentaDAO.guardar(nuevaCuenta);

            System.out.println(
                    "Cuenta guardada: " + cuentaGuardada
            );

        } catch (CuentaDuplicadaException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
        for (CuentaBancaria cuenta :
                cuentaDAO.listarCuentas()) {

            System.out.println(
                    "Cuenta BD: " + cuenta
            );

        }
        CuentaBancaria cuentaActualizar =
                cuentaDAO.buscarPorNumero("004")
                        .orElseThrow(() ->
                                new CuentaNoEncontradaException(
                                        "No existe la cuenta 001"
                                )
                        );

        System.out.println(
                "Antes del UPDATE: " + cuentaActualizar
        );

// Modificamos el objeto en Java
        cuentaActualizar.depositar(
                new BigDecimal("5000")
        );

// Guardamos el nuevo estado en PostgreSQL
        boolean cuentaActualizada =
                cuentaDAO.actualizar(cuentaActualizar);

        System.out.println(
                "Cuenta actualizada: " + cuentaActualizada
        );

// Volvemos a consultar PostgreSQL para comprobar
        cuentaDAO.buscarPorNumero("001")
                .ifPresent(cuenta ->
                        System.out.println(
                                "Después del UPDATE: " + cuenta
                        )
                );
        System.out.println(
                "Existe antes de eliminar: " +
                        cuentaDAO.buscarPorNumero("999").isPresent()
        );

        boolean cuentaEliminada =
                cuentaDAO.eliminarPorNumero("999");

        System.out.println(
                "Cuenta eliminada: " +
                        cuentaEliminada
        );

        System.out.println(
                "Existe después de eliminar: " +
                        cuentaDAO.buscarPorNumero("999").isPresent()
        );
        System.out.println("ANTES DEL ROLLBACK");

        cuentaDAO.buscarPorNumero("001")
                .ifPresent(System.out::println);

        try {

            cuentaDAO.transferir(
                    "001",
                    "999999",
                    new BigDecimal("5000")
            );

        } catch (CuentaNoEncontradaException e) {

            System.out.println(
                    "Error controlado: " + e.getMessage()
            );
        }

        System.out.println("DESPUÉS DEL ROLLBACK");

        cuentaDAO.buscarPorNumero("001")
                .ifPresent(System.out::println);


    }



}