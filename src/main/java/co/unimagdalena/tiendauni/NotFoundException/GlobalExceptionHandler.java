package co.unimagdalena.tiendauni.NotFoundException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        var body = ApiError.of(HttpStatus.NOT_FOUND, ex.getMessage(), req.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        var violations = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        var body = ApiError.of(HttpStatus.BAD_REQUEST, "Validation failed", req.getDescription(false), violations);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, WebRequest req) {
        var violations = ex.getConstraintViolations().stream()
                .map(cv -> new ApiError.FieldViolation(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();
        var body = ApiError.of(HttpStatus.BAD_REQUEST, "Constraint violation", req.getDescription(false), violations);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex, WebRequest req) {
        var body = ApiError.of(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getDescription(false), List.of());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, WebRequest req) {
        var body = ApiError.of(HttpStatus.CONFLICT, ex.getMessage(), req.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, WebRequest req) {
        var body = ApiError.of(HttpStatus.CONFLICT, ex.getMessage(), req.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex, WebRequest req) {
        var body = ApiError.of(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getDescription(false), List.of());
        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest req) {
        var body = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getDescription(false), List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
