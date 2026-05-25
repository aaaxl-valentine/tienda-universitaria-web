package co.unimagdalena.tiendauni.DTOs;

import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDTOs {
    public record CreateOrderItemRequest(
            @Positive(message = "La cantidad debe ser mayor a 0")
            Integer quantity,
            @Positive(message = "El precio unitario debe ser mayor a 0")
            BigDecimal unitPrice,
            Long orderId,
            @Positive(message = "El ID del producto debe ser válido")
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