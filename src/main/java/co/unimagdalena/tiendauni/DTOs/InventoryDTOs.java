package co.unimagdalena.tiendauni.DTOs;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InventoryDTOs {
    public record UpdateInventoryRequest(
            Integer newStock,
            Integer newMinimumStock,
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