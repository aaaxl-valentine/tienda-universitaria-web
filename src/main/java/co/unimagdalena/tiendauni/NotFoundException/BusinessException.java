package co.unimagdalena.tiendauni.NotFoundException;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}