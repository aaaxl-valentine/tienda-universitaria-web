package co.unimagdalena.tiendauni.DTOs;

import co.unimagdalena.tiendauni.entity.enums.OrderStatus;
import co.unimagdalena.tiendauni.DTOs.OrderItemDTOs.CreateOrderItemRequest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class OrderDTOs {
    public record CreateOrderRequest(
            OrderStatus status,
            Long customerId,
            Long addressId,
            List<CreateOrderItemRequest> items
    ) implements Serializable {}

    public record CancelOrderRequest(
            Long orderId,
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