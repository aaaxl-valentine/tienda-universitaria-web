package co.unimagdalena.tiendauni.DTOs;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDTOs {
    public record CreateOrderItemRequest(
            Integer quantity,
            BigDecimal unitPrice,
            Long orderId,
            Long productId
    ) implements Serializable {}

    public record OrderItemResponse(
            Long id,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal,
            Long orderId,
            Long productId
    ) implements Serializable {}
}