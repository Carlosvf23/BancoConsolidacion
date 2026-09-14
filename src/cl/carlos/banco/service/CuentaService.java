package cl.carlos.banco.service;

import cl.carlos.banco.dao.CuentaDAO;
import cl.carlos.banco.database.ConexionBD;
import cl.carlos.banco.exception.CuentaBloqueadaException;
import cl.carlos.banco.exception.CuentaNoEncontradaException;
import cl.carlos.banco.exception.MontoInvalidoException;
import cl.carlos.banco.exception.SaldoInsuficienteException;
import cl.carlos.banco.model.CuentaBancaria;
import cl.carlos.banco.model.EstadoCuenta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class CuentaService {

    private final CuentaDAO cuentaDAO;

    public CuentaService(CuentaDAO cuentaDAO) {
        this.cuentaDAO = cuentaDAO;
    }

    public boolean transferir(
            String numeroOrigen,
            String numeroDestino,
            BigDecimal monto
    ) {

        // ==========================================
        // 1. VALIDACIONES QUE NO NECESITAN LA BD
        // ==========================================

        if (monto == null ||
                monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new MontoInvalidoException(
                    "El monto debe ser mayor a 0"
            );
        }

        if (numeroOrigen.equals(numeroDestino)) {

            throw new IllegalArgumentException(
                    "La cuenta origen y destino deben ser diferentes"
            );
        }


        // ==========================================
        // 2. COMENZAR TRANSACCIÓN
        // ==========================================

        try (Connection conexion =
                     ConexionBD.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {

                // ==================================
                // 3. BUSCAR CUENTA ORIGEN
                // ==================================

                CuentaBancaria origen =
                        cuentaDAO
                                .buscarPorNumeroParaActualizar(
                                        conexion,
                                        numeroOrigen
                                )
                                .orElseThrow(() ->
                                        new CuentaNoEncontradaException(
                                                "No existe la cuenta origen " +
                                                        numeroOrigen
                                        )
                                );


                // ==================================
                // 4. BUSCAR CUENTA DESTINO
                // ==================================

                CuentaBancaria destino =
                        cuentaDAO
                                .buscarPorNumeroParaActualizar(
                                        conexion,
                                        numeroDestino
                                )
                                .orElseThrow(() ->
                                        new CuentaNoEncontradaException(
                                                "No existe la cuenta destino " +
                                                        numeroDestino
                                        )
                                );


                // ==================================
                // 5. REGLAS DEL NEGOCIO
                // ==================================

                if (origen.getEstado() !=
                        EstadoCuenta.ACTIVA) {

                    throw new CuentaBloqueadaException(
                            "La cuenta origen no está activa"
                    );
                }

                if (destino.getEstado() !=
                        EstadoCuenta.ACTIVA) {

                    throw new CuentaBloqueadaException(
                            "La cuenta destino no está activa"
                    );
                }

                if (origen.getSaldo()
                        .compareTo(monto) < 0) {

                    throw new SaldoInsuficienteException(
                            "Saldo insuficiente para transferir"
                    );
                }


                // ==================================
                // 6. CALCULAR NUEVOS SALDOS
                // ==================================

                BigDecimal nuevoSaldoOrigen =
                        origen.getSaldo()
                                .subtract(monto);

                BigDecimal nuevoSaldoDestino =
                        destino.getSaldo()
                                .add(monto);


                // ==================================
                // 7. DAO ACTUALIZA POSTGRESQL
                // ==================================

                cuentaDAO.actualizarSaldo(
                        conexion,
                        numeroOrigen,
                        nuevoSaldoOrigen
                );

                cuentaDAO.actualizarSaldo(
                        conexion,
                        numeroDestino,
                        nuevoSaldoDestino
                );


                // ==================================
                // 8. TODO FUNCIONÓ
                // ==================================

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