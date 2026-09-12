package cl.carlos.banco.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/banco_consolidacion";

    private static final String USUARIO = "postgres";

    private static final String PASSWORD = "contraseñabasededatos";

    public static Connection obtenerConexion() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USUARIO,
                PASSWORD
        );
    }
}