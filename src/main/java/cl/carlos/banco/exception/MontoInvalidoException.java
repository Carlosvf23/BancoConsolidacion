package cl.carlos.banco.exception;

public class MontoInvalidoException extends RuntimeException {

    public MontoInvalidoException(String mensaje) {
        super(mensaje);
    }
}