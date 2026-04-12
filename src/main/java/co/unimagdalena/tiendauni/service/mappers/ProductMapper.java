package co.unimagdalena.tiendauni.service.mappers;

import co.unimagdalena.tiendauni.DTOs.ProductDTOs.CreateProductRequest;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.ProductResponse;
import co.unimagdalena.tiendauni.entity.Category;
import co.unimagdalena.tiendauni.entity.Product;

public class ProductMapper {

    public static Product toEntity(CreateProductRequest request, Category category) {
        return Product.builder()
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .active(request.active())
                .category(category)
                .build();
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getActive(),
                product.getCreatedAt(),
                product.getCategory() != null ? product.getCategory().getId() : null
        );
    }
}
