package co.unimagdalena.tiendauni.DTOs;

import co.unimagdalena.tiendauni.entity.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CustomerDTOs {
    public record CreateCustomerRequest(
            @NotBlank(message = "El nombre no puede estar vacío")
            String firstName,
            @NotBlank(message = "El apellido no puede estar vacío")
            String lastName,
            @Email(message = "El email debe ser válido")
            @NotBlank(message = "El email no puede estar vacío")
            String email,
            CustomerStatus status
    ) implements Serializable {}

    public record UpdateCustomerRequest(
            @NotBlank(message = "El nombre no puede estar vacío")
            String firstName,
            @NotBlank(message = "El apellido no puede estar vacío")
            String lastName,
            @Email(message = "El email debe ser válido")
            String email,
            CustomerStatus status
    ) implements Serializable {}

    public record CustomerResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            CustomerStatus status,
            LocalDateTime createdAt
    ) implements Serializable {}

    public record TopCustomerResponse(
            Long customerId,
            String fullName,
            String email,
            java.math.BigDecimal totalRevenue
    ) implements Serializable {}
}