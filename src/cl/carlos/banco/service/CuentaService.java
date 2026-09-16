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


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CuentaService(
            CuentaDAO cuentaDAO
    ) {

        this.cuentaDAO =
                cuentaDAO;
    }


    // =========================================================
    // TRANSFERENCIA
    // =========================================================

    public boolean transferir(
            String numeroOrigen,
            String numeroDestino,
            BigDecimal monto
    ) {

        // =====================================================
        // 1. VALIDACIONES BÁSICAS
        // =====================================================

        if (monto == null ||
                monto.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new MontoInvalidoException(
                    "El monto debe ser mayor a 0"
            );
        }


        if (numeroOrigen == null ||
                numeroOrigen.isBlank()) {

            throw new IllegalArgumentException(
                    "La cuenta origen es obligatoria"
            );
        }


        if (numeroDestino == null ||
                numeroDestino.isBlank()) {

            throw new IllegalArgumentException(
                    "La cuenta destino es obligatoria"
            );
        }


        if (numeroOrigen.equals(
                numeroDestino
        )) {

            throw new IllegalArgumentException(
                    "La cuenta origen y destino deben ser diferentes"
            );
        }


        // =====================================================
        // 2. ABRIMOS UNA SOLA CONNECTION
        // =====================================================

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion()
        ) {

            conexion.setAutoCommit(
                    false
            );


            try {

                // =============================================
                // 3. BUSCAR CUENTA ORIGEN
                // =============================================

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


                // =============================================
                // 4. BUSCAR CUENTA DESTINO
                // =============================================

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


                // =============================================
                // 5. VALIDAR ESTADOS
                // =============================================

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


                // =============================================
                // 6. VALIDAR SALDO
                // =============================================

                if (
                        origen.getSaldo()
                                .compareTo(
                                        monto
                                ) < 0
                ) {

                    throw new SaldoInsuficienteException(
                            "Saldo insuficiente para transferir"
                    );
                }


                // =============================================
                // 7. CALCULAR NUEVOS SALDOS
                // =============================================

                BigDecimal nuevoSaldoOrigen =
                        origen.getSaldo()
                                .subtract(
                                        monto
                                );

                BigDecimal nuevoSaldoDestino =
                        destino.getSaldo()
                                .add(
                                        monto
                                );


                // =============================================
                // 8. ACTUALIZAR CUENTA ORIGEN
                // =============================================

                boolean origenActualizado =
                        cuentaDAO.actualizarSaldo(
                                conexion,
                                numeroOrigen,
                                nuevoSaldoOrigen
                        );


                if (!origenActualizado) {

                    throw new RuntimeException(
                            "No se pudo actualizar la cuenta origen"
                    );
                }


                // =============================================
                // 9. ACTUALIZAR CUENTA DESTINO
                // =============================================

                boolean destinoActualizado =
                        cuentaDAO.actualizarSaldo(
                                conexion,
                                numeroDestino,
                                nuevoSaldoDestino
                        );


                if (!destinoActualizado) {

                    throw new RuntimeException(
                            "No se pudo actualizar la cuenta destino"
                    );
                }


                // =============================================
                // 10. CONFIRMAR TRANSACCIÓN
                // =============================================

                conexion.commit();

                return true;


            } catch (Exception e) {

                // =============================================
                // ALGO FALLÓ → DESHACEMOS TODO
                // =============================================

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