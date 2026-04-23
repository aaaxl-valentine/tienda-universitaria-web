package co.unimagdalena.tiendauni.NotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest request = mock(WebRequest.class);

    @Test
    void shouldReturnNotFoundForResourceNotFoundException() {
        when(request.getDescription(false)).thenReturn("uri=/api/customers/1");
        var response = handler.handleNotFound(new ResourceNotFoundException("No existe"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("No existe");
    }

    @Test
    void shouldReturnBadRequestForIllegalArgumentException() {
        when(request.getDescription(false)).thenReturn("uri=/api/products");
        var response = handler.handleIllegalArg(new IllegalArgumentException("Dato invalido"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Dato invalido");
    }

    @Test
    void shouldReturnConflictForConflictException() {
        when(request.getDescription(false)).thenReturn("uri=/api/orders/1");
        var response = handler.handleConflict(new ConflictException("Conflicto"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Conflicto");
    }

    @Test
    void shouldReturnInternalServerErrorForUnknownException() {
        when(request.getDescription(false)).thenReturn("uri=/api/reports");
        var response = handler.handleGeneric(new RuntimeException("Error inesperado"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Error inesperado");
    }
}
