package cl.carlos.banco.exception;

public class PersonaConCuentasException extends RuntimeException {

    public PersonaConCuentasException(String mensaje) {
        super(mensaje);
    }
}