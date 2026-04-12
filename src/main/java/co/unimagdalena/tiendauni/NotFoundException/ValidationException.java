package co.unimagdalena.tiendauni.NotFoundException;

public class ValidationException  extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}