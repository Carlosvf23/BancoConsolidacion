package cl.carlos.banco;

import cl.carlos.banco.dao.CuentaDAO;
import cl.carlos.banco.dao.PersonaDAO;
import cl.carlos.banco.database.ConexionBD;
import cl.carlos.banco.exception.*;
import cl.carlos.banco.model.*;
import cl.carlos.banco.service.CuentaService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        // =====================================================
        // 1. PROBAR CONEXIÓN A POSTGRESQL
        // =====================================================

        try (Connection conexion = ConexionBD.obtenerConexion()) {

            System.out.println(
                    "Conexión a PostgreSQL exitosa"
            );

        } catch (SQLException e) {

            System.out.println(
                    "Error de conexión: " +
                            e.getMessage()
            );
        }


        // =====================================================
        // 2. PERSONAS
        // =====================================================

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


        // =====================================================
        // 3. BANCO Y CUENTAS EN MEMORIA
        // =====================================================

        Banco banco = new Banco();

        CuentaBancaria cuentaVista =
                new CuentaVista(
                        "003",
                        persona1
                );

        CuentaBancaria cuentaVista2 =
                new CuentaVista(
                        "004",
                        persona2
                );

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
                "Saldo corriente: " +
                        cuentaCorriente.getSaldo()
        );

        System.out.println(
                "Saldo vista: " +
                        cuentaVista.getSaldo()
        );

        System.out.println(
                "Cuentas activas: " +
                        banco.contarCuentasPorEstado(
                                EstadoCuenta.ACTIVA
                        )
        );


        // =====================================================
        // 4. PERSONA DAO
        // =====================================================

        PersonaDAO personaDAO =
                new PersonaDAO();

        for (Persona persona :
                personaDAO.listarPersonas()) {

            System.out.println(
                    "Desde PostgreSQL: " +
                            persona
            );
        }

        personaDAO.buscarPorRut(
                        "12345678-9"
                )
                .ifPresent(persona ->
                        System.out.println(
                                "Encontrada: " +
                                        persona
                        )
                );

        System.out.println(
                personaDAO.buscarPorRut(
                        "99999999-9"
                )
        );


        // =====================================================
        // 5. GUARDAR PERSONA
        // =====================================================

        Persona persona3 =
                new Persona(
                        "Mariela",
                        "11111118-1",
                        LocalDate.of(
                                1998,
                                5,
                                15
                        )
                );

        try {

            boolean guardada =
                    personaDAO.guardar(
                            persona3
                    );

            System.out.println(
                    "Persona guardada: " +
                            guardada
            );

        } catch (PersonaDuplicadaException e) {

            System.out.println(
                    "No se pudo guardar: " +
                            e.getMessage()
            );
        }

        personaDAO.buscarPorRut(
                        "11111118-1"
                )
                .ifPresent(persona ->
                        System.out.println(
                                "Desde BD: " +
                                        persona
                        )
                );


        // =====================================================
        // 6. ACTUALIZAR PERSONA
        // =====================================================

        Persona personaActualizada =
                new Persona(
                        "Andrea Actualizada",
                        "11111118-1",
                        LocalDate.of(
                                1998,
                                5,
                                15
                        )
                );

        boolean actualizada =
                personaDAO.actualizar(
                        personaActualizada
                );

        System.out.println(
                "Persona actualizada: " +
                        actualizada
        );

        personaDAO.buscarPorRut(
                        "11111118-1"
                )
                .ifPresent(
                        System.out::println
                );


        // =====================================================
        // 7. PROBAR FOREIGN KEY PERSONA
        // =====================================================

        try {

            personaDAO.eliminarPorRut(
                    "12345678-9"
            );

        } catch (PersonaConCuentasException e) {

            System.out.println(
                    "Error: " +
                            e.getMessage()
            );
        }


        // =====================================================
        // 8. CUENTA DAO
        // =====================================================

        CuentaDAO cuentaDAO =
                new CuentaDAO();


        // =====================================================
        // 9. CUENTA SERVICE
        // =====================================================

        CuentaService cuentaService =
                new CuentaService(
                        cuentaDAO
                );


        // =====================================================
        // 10. BUSCAR CUENTA
        // =====================================================

        cuentaDAO.buscarPorNumero(
                        "001"
                )
                .ifPresent(cuenta ->
                        System.out.println(
                                "Cuenta desde PostgreSQL: " +
                                        cuenta
                        )
                );

        System.out.println(
                cuentaDAO.buscarPorNumero(
                        "999"
                )
        );


        // =====================================================
        // 11. GUARDAR CUENTAS
        // =====================================================

        CuentaBancaria nuevaCuenta =
                new CuentaVista(
                        "010",
                        persona1
                );

        CuentaCorriente cuentaCredito =
                new CuentaCorriente(
                        "011",
                        persona1,
                        new BigDecimal(
                                "500000"
                        )
                );

        try {

            cuentaDAO.guardar(
                    cuentaCredito
            );

            System.out.println(
                    "Cuenta corriente guardada correctamente"
            );

        } catch (CuentaDuplicadaException e) {

            System.out.println(
                    "Error: " +
                            e.getMessage()
            );
        }

        try {

            boolean cuentaGuardada =
                    cuentaDAO.guardar(
                            nuevaCuenta
                    );

            System.out.println(
                    "Cuenta guardada: " +
                            cuentaGuardada
            );

        } catch (CuentaDuplicadaException e) {

            System.out.println(
                    "Error: " +
                            e.getMessage()
            );
        }


        // =====================================================
        // 12. LISTAR CUENTAS
        // =====================================================

        for (CuentaBancaria cuenta :
                cuentaDAO.listarCuentas()) {

            System.out.println(
                    "Cuenta BD: " +
                            cuenta
            );
        }


        // =====================================================
        // 13. ACTUALIZAR CUENTA
        // =====================================================

        CuentaBancaria cuentaActualizar =
                cuentaDAO.buscarPorNumero(
                                "004"
                        )
                        .orElseThrow(() ->
                                new CuentaNoEncontradaException(
                                        "No existe la cuenta 004"
                                )
                        );

        System.out.println(
                "Antes del UPDATE: " +
                        cuentaActualizar
        );

        cuentaActualizar.depositar(
                new BigDecimal("5000")
        );

        boolean cuentaActualizada =
                cuentaDAO.actualizar(
                        cuentaActualizar
                );

        System.out.println(
                "Cuenta actualizada: " +
                        cuentaActualizada
        );

        cuentaDAO.buscarPorNumero(
                        "004"
                )
                .ifPresent(cuenta ->
                        System.out.println(
                                "Después del UPDATE: " +
                                        cuenta
                        )
                );


        // =====================================================
        // 14. ELIMINAR CUENTA
        // =====================================================

        System.out.println(
                "Existe antes de eliminar: " +
                        cuentaDAO
                                .buscarPorNumero(
                                        "999"
                                )
                                .isPresent()
        );

        boolean cuentaEliminada =
                cuentaDAO.eliminarPorNumero(
                        "999"
                );

        System.out.println(
                "Cuenta eliminada: " +
                        cuentaEliminada
        );

        System.out.println(
                "Existe después de eliminar: " +
                        cuentaDAO
                                .buscarPorNumero(
                                        "999"
                                )
                                .isPresent()
        );


        // =====================================================
        // 15. PROBAR ROLLBACK
        // =====================================================

        System.out.println(
                "ANTES DEL ROLLBACK"
        );

        cuentaDAO.buscarPorNumero(
                        "001"
                )
                .ifPresent(
                        System.out::println
                );

        try {

            cuentaDAO.transferir(
                    "001",
                    "999999",
                    new BigDecimal(
                            "5000"
                    )
            );

        } catch (CuentaNoEncontradaException e) {

            System.out.println(
                    "Error controlado: " +
                            e.getMessage()
            );
        }

        System.out.println(
                "DESPUÉS DEL ROLLBACK"
        );

        cuentaDAO.buscarPorNumero(
                        "001"
                )
                .ifPresent(
                        System.out::println
                );


        // =====================================================
        // 16. PROBAR CUENTA BLOQUEADA
        // =====================================================

        CuentaBancaria cuentaBloqueada =
                cuentaDAO.buscarPorNumero(
                                "001"
                        )
                        .orElseThrow(() ->
                                new CuentaNoEncontradaException(
                                        "No existe la cuenta 001"
                                )
                        );

        cuentaBloqueada.bloquearCuenta();

        cuentaDAO.actualizar(
                cuentaBloqueada
        );

        System.out.println(
                "Estado de la cuenta: " +
                        cuentaBloqueada.getEstado()
        );

        try {

            cuentaDAO.transferir(
                    "001",
                    "003",
                    new BigDecimal(
                            "5000"
                    )
            );

        } catch (CuentaBloqueadaException e) {

            System.out.println(
                    "Transferencia rechazada: " +
                            e.getMessage()
            );
        }


        // Restauramos la cuenta
        cuentaBloqueada.activarCuenta();

        cuentaDAO.actualizar(
                cuentaBloqueada
        );

        System.out.println(
                "Estado restaurado: " +
                        cuentaBloqueada.getEstado()
        );


        // =====================================================
        // 17. TRANSFERENCIA USANDO CUENTASERVICE
        // =====================================================

        try {

            boolean transferenciaRealizada =
                    cuentaService.transferir(
                            "001",
                            "003",
                            new BigDecimal(
                                    "5000"
                            )
                    );

            System.out.println(
                    "Transferencia con Service realizada: " +
                            transferenciaRealizada
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "No se pudo realizar la transferencia: " +
                            e.getMessage()
            );
        }

        System.out.println();
        System.out.println("===== PRUEBA ROLLBACK DESDE SERVICE =====");

        BigDecimal saldoAntes =
                cuentaDAO.buscarPorNumero("001")
                        .orElseThrow()
                        .getSaldo();

        try {

            cuentaService.transferir(
                    "001",
                    "999999",
                    new BigDecimal("5000")
            );

        } catch (CuentaNoEncontradaException e) {

            System.out.println(
                    "Error esperado: " +
                            e.getMessage()
            );
        }

        BigDecimal saldoDespues =
                cuentaDAO.buscarPorNumero("001")
                        .orElseThrow()
                        .getSaldo();

        System.out.println(
                "Saldo antes: " + saldoAntes
        );

        System.out.println(
                "Saldo después: " + saldoDespues
        );

    }
}