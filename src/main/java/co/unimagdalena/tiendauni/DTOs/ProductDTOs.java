package co.unimagdalena.tiendauni.DTOs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDTOs {
    public record CreateProductRequest(
            String sku,
            String name,
            String description,
            BigDecimal price,
            boolean active,
            Long categoryId
    ) implements Serializable {}

    public record UpdateProductRequest(
            String name,
            String description,
            BigDecimal price,
            Boolean active,
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