package co.unimagdalena.tiendauni.DTOs;

import co.unimagdalena.tiendauni.entity.enums.OrderStatus;
import co.unimagdalena.tiendauni.DTOs.OrderItemDTOs.CreateOrderItemRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class OrderDTOs {
    public record CreateOrderRequest(
            @NotNull(message = "El estado no puede ser nulo")
            OrderStatus status,
            @Positive(message = "El ID del cliente debe ser válido")
            Long customerId,
            @Positive(message = "El ID de la dirección debe ser válido")
            Long addressId,
            @NotEmpty(message = "La orden debe tener al menos un item")
            List<CreateOrderItemRequest> items
    ) implements Serializable {}

    public record CancelOrderRequest(
            @Positive(message = "El ID de la orden debe ser válido")
            Long orderId,
            @NotNull(message = "La razón de cancelación no puede ser nula")
            String reason
    ) implements Serializable {}

    public record OrderResponse(
            Long id,
            OrderStatus status,
            BigDecimal total,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long customerId,
            Long addressId
    ) implements Serializable {}

    public record MonthlyIncomeResponse(
            YearMonth month,
            BigDecimal totalIncome
    ) implements Serializable {}
}