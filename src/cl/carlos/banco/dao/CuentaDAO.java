package cl.carlos.banco.dao;

import cl.carlos.banco.database.ConexionBD;
import cl.carlos.banco.exception.CuentaDuplicadaException;
import cl.carlos.banco.exception.CuentaNoEncontradaException;
import cl.carlos.banco.model.CuentaBancaria;
import cl.carlos.banco.model.CuentaCorriente;
import cl.carlos.banco.model.CuentaVista;
import cl.carlos.banco.model.EstadoCuenta;
import cl.carlos.banco.model.Persona;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class CuentaDAO {

    public Optional<CuentaBancaria> buscarPorNumero(String numeroCuenta) {

        String sql = """
                SELECT
                    c.numero_cuenta,
                    c.saldo,
                    c.estado,
                    c.tipo_cuenta,
                    c.linea_credito,
                    p.rut,
                    p.nombre,
                    p.fecha_nacimiento
                FROM cuentas c
                JOIN personas p
                    ON p.rut = c.titular_rut
                WHERE c.numero_cuenta = ?
                """;

        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(1, numeroCuenta);

            try (ResultSet resultado = statement.executeQuery()) {

                if (!resultado.next()) {
                    return Optional.empty();
                }

                Persona titular = new Persona(
                        resultado.getString("nombre"),
                        resultado.getString("rut"),
                        resultado.getDate("fecha_nacimiento")
                                .toLocalDate()
                );

                CuentaBancaria cuenta;

                String tipoCuenta =
                        resultado.getString("tipo_cuenta");

                if ("VISTA".equals(tipoCuenta)) {

                    cuenta = new CuentaVista(
                            resultado.getString("numero_cuenta"),
                            titular
                    );

                } else if ("CORRIENTE".equals(tipoCuenta)) {

                    cuenta = new CuentaCorriente(
                            resultado.getString("numero_cuenta"),
                            titular,
                            resultado.getBigDecimal("linea_credito")
                    );

                } else {

                    throw new RuntimeException(
                            "Tipo de cuenta desconocido: " + tipoCuenta
                    );
                }

                BigDecimal saldo =
                        resultado.getBigDecimal("saldo");

                if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                    cuenta.depositar(saldo);
                }

                EstadoCuenta estado = EstadoCuenta.valueOf(
                        resultado.getString("estado")
                );

                if (estado == EstadoCuenta.BLOQUEADA) {
                    cuenta.bloquearCuenta();
                }

                if (estado == EstadoCuenta.CERRADA) {
                    cuenta.cerrarCuenta();
                }

                return Optional.of(cuenta);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar la cuenta",
                    e
            );


        }

    }
    public boolean guardar(CuentaBancaria cuenta) {

        String sql = """
            INSERT INTO cuentas (
                numero_cuenta,
                titular_rut,
                saldo,
                estado,
                tipo_cuenta,
                linea_credito
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    cuenta.getNumeroCuenta()
            );

            statement.setString(
                    2,
                    cuenta.getTitular().getRut()
            );

            statement.setBigDecimal(
                    3,
                    cuenta.getSaldo()
            );

            statement.setString(
                    4,
                    cuenta.getEstado().name()
            );

            if (cuenta instanceof CuentaCorriente corriente) {

                statement.setString(
                        5,
                        "CORRIENTE"
                );

                statement.setBigDecimal(
                        6,
                        corriente.getLineaCredito()
                );

            } else if (cuenta instanceof CuentaVista) {

                statement.setString(
                        5,
                        "VISTA"
                );

                statement.setNull(
                        6,
                        java.sql.Types.NUMERIC
                );

            } else {

                throw new RuntimeException(
                        "Tipo de cuenta no soportado"
                );
            }

            int filasAfectadas =
                    statement.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                throw new CuentaDuplicadaException(
                        "Ya existe la cuenta "
                                + cuenta.getNumeroCuenta()
                );
            }

            throw new RuntimeException(
                    "Error al guardar la cuenta",
                    e
            );
        }

    }
    private CuentaBancaria crearCuentaDesdeResultado(
            ResultSet resultado
    ) throws SQLException {

        Persona titular = new Persona(
                resultado.getString("nombre"),
                resultado.getString("rut"),
                resultado.getDate("fecha_nacimiento").toLocalDate()
        );

        String tipoCuenta =
                resultado.getString("tipo_cuenta");

        CuentaBancaria cuenta;

        if ("VISTA".equals(tipoCuenta)) {

            cuenta = new CuentaVista(
                    resultado.getString("numero_cuenta"),
                    titular
            );

        } else if ("CORRIENTE".equals(tipoCuenta)) {

            cuenta = new CuentaCorriente(
                    resultado.getString("numero_cuenta"),
                    titular,
                    resultado.getBigDecimal("linea_credito")
            );

        } else {

            throw new RuntimeException(
                    "Tipo de cuenta desconocido: " + tipoCuenta
            );
        }

        BigDecimal saldo =
                resultado.getBigDecimal("saldo");

        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            cuenta.depositar(saldo);
        }

        EstadoCuenta estado = EstadoCuenta.valueOf(
                resultado.getString("estado")
        );

        if (estado == EstadoCuenta.BLOQUEADA) {
            cuenta.bloquearCuenta();
        }

        if (estado == EstadoCuenta.CERRADA) {
            cuenta.cerrarCuenta();
        }

        return cuenta;
    }
    public List<CuentaBancaria> listarCuentas() {

        List<CuentaBancaria> cuentas =
                new ArrayList<>();

        String sql = """
            SELECT
                c.numero_cuenta,
                c.saldo,
                c.estado,
                c.tipo_cuenta,
                c.linea_credito,
                p.rut,
                p.nombre,
                p.fecha_nacimiento
            FROM cuentas c
            JOIN personas p
                ON p.rut = c.titular_rut
            """;

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql);

                ResultSet resultado =
                        statement.executeQuery()
        ) {

            while (resultado.next()) {

                CuentaBancaria cuenta =
                        crearCuentaDesdeResultado(resultado);

                cuentas.add(cuenta);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar las cuentas",
                    e
            );
        }

        return cuentas;
    }
    public boolean actualizar(CuentaBancaria cuenta) {

        String sql = """
            UPDATE cuentas
            SET titular_rut = ?,
                saldo = ?,
                estado = ?,
                tipo_cuenta = ?,
                linea_credito = ?
            WHERE numero_cuenta = ?
            """;

        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    cuenta.getTitular().getRut()
            );

            statement.setBigDecimal(
                    2,
                    cuenta.getSaldo()
            );

            statement.setString(
                    3,
                    cuenta.getEstado().name()
            );

            if (cuenta instanceof CuentaCorriente corriente) {

                statement.setString(
                        4,
                        "CORRIENTE"
                );

                statement.setBigDecimal(
                        5,
                        corriente.getLineaCredito()
                );

            } else if (cuenta instanceof CuentaVista) {

                statement.setString(
                        4,
                        "VISTA"
                );

                statement.setNull(
                        5,
                        java.sql.Types.NUMERIC
                );

            } else {

                throw new RuntimeException(
                        "Tipo de cuenta no soportado"
                );
            }

            statement.setString(
                    6,
                    cuenta.getNumeroCuenta()
            );

            int filasAfectadas =
                    statement.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al actualizar la cuenta",
                    e
            );
        }

    }
    public boolean eliminarPorNumero(String numeroCuenta) {

        String sql = """
            DELETE FROM cuentas
            WHERE numero_cuenta = ?
            """;

        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    numeroCuenta
            );

            int filasAfectadas =
                    statement.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al eliminar la cuenta",
                    e
            );
        }

    }
    public boolean transferir(
            String numeroOrigen,
            String numeroDestino,
            BigDecimal monto
    ) {

        String sqlRetirar = """
            UPDATE cuentas
            SET saldo = saldo - ?
            WHERE numero_cuenta = ?
            """;

        String sqlDepositar = """
            UPDATE cuentas
            SET saldo = saldo + ?
            WHERE numero_cuenta = ?
            """;

        try (Connection conexion =
                     ConexionBD.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try (
                    PreparedStatement retirar =
                            conexion.prepareStatement(sqlRetirar);

                    PreparedStatement depositar =
                            conexion.prepareStatement(sqlDepositar)
            ) {

                retirar.setBigDecimal(1, monto);
                retirar.setString(2, numeroOrigen);

                int filasOrigen =
                        retirar.executeUpdate();

                if (filasOrigen == 0) {
                    throw new CuentaNoEncontradaException(
                            "No existe la cuenta origen " +
                                    numeroOrigen
                    );
                }

                depositar.setBigDecimal(1, monto);
                depositar.setString(2, numeroDestino);

                int filasDestino =
                        depositar.executeUpdate();

                if (filasDestino == 0) {
                    throw new CuentaNoEncontradaException(
                            "No existe la cuenta destino " +
                                    numeroDestino
                    );
                }

                conexion.commit();

                return true;

            } catch (Exception e) {

                conexion.rollback();

                throw e;
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error durante la transferencia",
                    e
            );
        }
    }
}