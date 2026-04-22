package co.unimagdalena.tiendauni.DTOs;

import co.unimagdalena.tiendauni.entity.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CustomerDTOs {
    public record CreateCustomerRequest(

            @NotBlank  String firstName,
            String lastName,
            @Email @NotBlank String email,
            CustomerStatus status
    ) implements Serializable {}

    public record UpdateCustomerRequest(
            String firstName,
            String lastName,
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