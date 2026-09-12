package cl.carlos.banco.exception;

public class PersonaDuplicadaException extends RuntimeException {

    public PersonaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}