package cl.carlos.banco;

import cl.carlos.banco.dao.CuentaDAO;
import cl.carlos.banco.dao.PersonaDAO;
import cl.carlos.banco.database.ConexionBD;
import cl.carlos.banco.model.CuentaBancaria;
import cl.carlos.banco.model.Persona;
import cl.carlos.banco.service.CuentaService;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        // =====================================================
        // 1. COMPROBAR CONEXIÓN
        // =====================================================

        try (Connection conexion =
                     ConexionBD.obtenerConexion()) {

            System.out.println(
                    "Conexión a PostgreSQL exitosa"
            );

        } catch (SQLException e) {

            System.out.println(
                    "Error de conexión: " +
                            e.getMessage()
            );

            return;
        }


        // =====================================================
        // 2. CREAR CAPAS
        // =====================================================

        PersonaDAO personaDAO =
                new PersonaDAO();

        CuentaDAO cuentaDAO =
                new CuentaDAO();

        CuentaService cuentaService =
                new CuentaService(cuentaDAO);


        // =====================================================
        // 3. MOSTRAR PERSONAS
        // =====================================================

        System.out.println();
        System.out.println("===== PERSONAS =====");

        for (Persona persona :
                personaDAO.listarPersonas()) {

            System.out.println(persona);
        }


        // =====================================================
        // 4. MOSTRAR CUENTAS
        // =====================================================

        System.out.println();
        System.out.println("===== CUENTAS =====");

        for (CuentaBancaria cuenta :
                cuentaDAO.listarCuentas()) {

            System.out.println(cuenta);
        }


        // =====================================================
        // 5. APLICACIÓN LISTA
        // =====================================================

        System.out.println();
        System.out.println(
                "BancoConsolidacion iniciado correctamente"
        );
    }
}