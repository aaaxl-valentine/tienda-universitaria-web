package co.unimagdalena.tiendauni.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InventoryDTOs {
    public record UpdateInventoryRequest(
            @Positive(message = "El stock debe ser mayor a 0")
            Integer newStock,
            @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
            Integer newMinimumStock,
            @Positive(message = "El ID del producto debe ser válido")
            Long productId
    ) implements Serializable {}

    public record InventoryResponse(
            Long id,
            Integer availableStock,
            Integer minimumStock,
            LocalDateTime updatedAt,
            Long productId
    ) implements Serializable {}
}