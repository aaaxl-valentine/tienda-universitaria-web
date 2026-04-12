package co.unimagdalena.tiendauni.NotFoundException;

public class ConflictException  extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}