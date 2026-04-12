package co.unimagdalena.tiendauni.service.mapper;

import co.unimagdalena.tiendauni.DTOs.CategoryDTOs.CreateCategoryRequest;
import co.unimagdalena.tiendauni.DTOs.CategoryDTOs.CategoryResponse;
import co.unimagdalena.tiendauni.entity.Category;

public class CategoryMapper {

    public static Category toEntity(CreateCategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
