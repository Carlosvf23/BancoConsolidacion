package cl.carlos.banco.dao;

import cl.carlos.banco.database.ConexionBD;
import cl.carlos.banco.exception.*;
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

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MontoInvalidoException(
                    "El monto debe ser mayor a 0"
            );
        }

        String sqlBuscarCuenta = """
            SELECT saldo, estado
            FROM cuentas
            WHERE numero_cuenta = ?
            FOR UPDATE
            """;

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

            try {

                // ==========================
                // 1. BUSCAR CUENTA ORIGEN
                // ==========================

                BigDecimal saldoOrigen;
                EstadoCuenta estadoOrigen;

                try (PreparedStatement statement =
                             conexion.prepareStatement(
                                     sqlBuscarCuenta
                             )) {

                    statement.setString(
                            1,
                            numeroOrigen
                    );

                    try (ResultSet resultado =
                                 statement.executeQuery()) {

                        if (!resultado.next()) {
                            throw new CuentaNoEncontradaException(
                                    "No existe la cuenta origen " +
                                            numeroOrigen
                            );
                        }

                        saldoOrigen =
                                resultado.getBigDecimal("saldo");

                        estadoOrigen =
                                EstadoCuenta.valueOf(
                                        resultado.getString("estado")
                                );
                    }
                }


                // ==========================
                // 2. BUSCAR CUENTA DESTINO
                // ==========================

                EstadoCuenta estadoDestino;

                try (PreparedStatement statement =
                             conexion.prepareStatement(
                                     sqlBuscarCuenta
                             )) {

                    statement.setString(
                            1,
                            numeroDestino
                    );

                    try (ResultSet resultado =
                                 statement.executeQuery()) {

                        if (!resultado.next()) {
                            throw new CuentaNoEncontradaException(
                                    "No existe la cuenta destino " +
                                            numeroDestino
                            );
                        }

                        estadoDestino =
                                EstadoCuenta.valueOf(
                                        resultado.getString("estado")
                                );
                    }
                }


                // ==========================
                // 3. VALIDACIONES
                // ==========================

                if (estadoOrigen != EstadoCuenta.ACTIVA) {

                    throw new CuentaBloqueadaException(
                            "La cuenta origen no está activa"
                    );
                }

                if (estadoDestino != EstadoCuenta.ACTIVA) {

                    throw new CuentaBloqueadaException(
                            "La cuenta destino no está activa"
                    );
                }

                if (saldoOrigen.compareTo(monto) < 0) {

                    throw new SaldoInsuficienteException(
                            "Saldo insuficiente para transferir"
                    );
                }


                // ==========================
                // 4. RETIRAR DEL ORIGEN
                // ==========================

                try (PreparedStatement statement =
                             conexion.prepareStatement(
                                     sqlRetirar
                             )) {

                    statement.setBigDecimal(
                            1,
                            monto
                    );

                    statement.setString(
                            2,
                            numeroOrigen
                    );

                    statement.executeUpdate();
                }


                // ==========================
                // 5. DEPOSITAR EN DESTINO
                // ==========================

                try (PreparedStatement statement =
                             conexion.prepareStatement(
                                     sqlDepositar
                             )) {

                    statement.setBigDecimal(
                            1,
                            monto
                    );

                    statement.setString(
                            2,
                            numeroDestino
                    );

                    statement.executeUpdate();
                }


                // ==========================
                // 6. CONFIRMAR
                // ==========================

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

    }public Optional<CuentaBancaria> buscarPorNumeroParaActualizar(
            Connection conexion,
            String numeroCuenta
    ) {

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
            FOR UPDATE OF c
            """;

        try (PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(
                    1,
                    numeroCuenta
            );

            try (ResultSet resultado =
                         statement.executeQuery()) {

                if (resultado.next()) {

                    return Optional.of(
                            crearCuentaDesdeResultado(
                                    resultado
                            )
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar la cuenta para actualizar",
                    e
            );
        }

    }public boolean actualizarSaldo(
            Connection conexion,
            String numeroCuenta,
            BigDecimal nuevoSaldo
    ) {

        String sql = """
            UPDATE cuentas
            SET saldo = ?
            WHERE numero_cuenta = ?
            """;

        try (PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setBigDecimal(
                    1,
                    nuevoSaldo
            );

            statement.setString(
                    2,
                    numeroCuenta
            );

            int filasAfectadas =
                    statement.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al actualizar el saldo",
                    e
            );
        }
    }


}