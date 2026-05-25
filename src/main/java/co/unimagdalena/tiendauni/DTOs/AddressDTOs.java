package co.unimagdalena.tiendauni.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;

public class AddressDTOs {
    public record CreateAddressRequest(
            @NotBlank(message = "La calle no puede estar vacía")
            String street,
            @NotBlank(message = "La ciudad no puede estar vacía")
            String city,
            @NotBlank(message = "El departamento no puede estar vacío")
            String department,
            @NotBlank(message = "El código postal no puede estar vacío")
            String postalCode,
            boolean isDefault,
            @Positive(message = "El ID del cliente debe ser válido")
            Long customerId
    ) implements Serializable {}

    public record AddressResponse(
            Long id,
            String street,
            String city,
            String department,
            String postalCode,
            boolean isDefault,
            Long customerId
    ) implements Serializable {}
}