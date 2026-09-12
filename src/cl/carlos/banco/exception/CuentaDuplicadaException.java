package cl.carlos.banco.exception;

public class CuentaDuplicadaException extends RuntimeException {

    public CuentaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}