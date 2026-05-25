package co.unimagdalena.tiendauni.DTOs;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDTOs {
    public record CreateProductRequest(
            @NotBlank(message = "El SKU no puede estar vacío")
            String sku,
            @NotBlank(message = "El nombre del producto no puede estar vacío")
            String name,
            @NotBlank(message = "La descripción no puede estar vacía")
            String description,
            @Positive(message = "El precio debe ser mayor a 0")
            BigDecimal price,
            boolean active,
            @Positive(message = "El ID de categoría debe ser válido")
            Long categoryId
    ) implements Serializable {}

    public record UpdateProductRequest(
            @NotBlank(message = "El nombre no puede estar vacío")
            String name,
            @NotBlank(message = "La descripción no puede estar vacía")
            String description,
            @Positive(message = "El precio debe ser mayor a 0")
            BigDecimal price,
            Boolean active,
            @Positive(message = "El ID de categoría debe ser válido")
            Long categoryId
    ) implements Serializable {}

    public record ProductResponse(
            Long id,
            String sku,
            String name,
            String description,
            BigDecimal price,
            boolean active,
            LocalDateTime createdAt,
            Long categoryId
    ) implements Serializable {}

    public record BestSellingProductResponse(
            Long productId,
            String sku,
            String name,
            Long totalUnitsSold
    ) implements Serializable {}

    public record LowStockProductResponse(
            Long productId,
            String sku,
            String name,
            Integer availableStock,
            Integer minimumStock
    ) implements Serializable {}
}