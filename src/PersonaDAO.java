import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonaDAO {

    public List<Persona> listarPersonas() {

        List<Persona> personas = new ArrayList<>();

        String sql = """
                SELECT rut, nombre, fecha_nacimiento
                FROM personas
                """;

        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {

            while (resultado.next()) {

                Persona persona = new Persona(
                        resultado.getString("nombre"),
                        resultado.getString("rut"),
                        resultado.getDate("fecha_nacimiento")
                                .toLocalDate()
                );

                personas.add(persona);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al obtener las personas",
                    e
            );
        }

        return personas;
    }
    public Optional<Persona> buscarPorRut(String rut) {

        String sql = """
            SELECT rut, nombre, fecha_nacimiento
            FROM personas
            WHERE rut = ?
            """;

        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, rut);

            try (ResultSet resultado = statement.executeQuery()) {

                if (resultado.next()) {

                    Persona persona = new Persona(
                            resultado.getString("nombre"),
                            resultado.getString("rut"),
                            resultado.getDate("fecha_nacimiento")
                                    .toLocalDate()
                    );

                    return Optional.of(persona);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar la persona",
                    e
            );
        }

    }
    public boolean guardar(Persona persona) {

        String sql = """
            INSERT INTO personas (rut, nombre, fecha_nacimiento)
            VALUES (?, ?, ?)
            """;

        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, persona.getRut());
            statement.setString(2, persona.getNombre());
            statement.setDate(
                    3,
                    java.sql.Date.valueOf(persona.getFechaNacimiento())
            );

            int filasAfectadas = statement.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                throw new PersonaDuplicadaException(
                        "Ya existe una persona con el RUT " + persona.getRut()
                );
            }

            throw new RuntimeException(
                    "Error al guardar la persona",
                    e
            );
        }
    }
}