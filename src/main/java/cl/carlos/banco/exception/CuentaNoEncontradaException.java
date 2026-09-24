package cl.carlos.banco.exception;

public class CuentaNoEncontradaException extends RuntimeException {

    public CuentaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}