package co.unimagdalena.tiendauni.DTOs;

import co.unimagdalena.tiendauni.enums.CustomerStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CustomerDTOs {
    public record CreateCustomerRequest(
            String firstName,
            String lastName,
            String email,
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