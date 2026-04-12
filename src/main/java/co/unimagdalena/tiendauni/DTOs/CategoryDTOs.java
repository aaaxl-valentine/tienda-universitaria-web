package co.unimagdalena.tiendauni.DTOs;

import java.io.Serializable;

public class CategoryDTOs {
    public record CreateCategoryRequest(
            String name,
            String description
    ) implements Serializable {}

    public record CategoryResponse (
            Long id,
            String name,
            String description
    ) implements Serializable {}
}