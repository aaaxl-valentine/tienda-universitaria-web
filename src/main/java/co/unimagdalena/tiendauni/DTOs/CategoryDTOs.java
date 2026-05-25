package co.unimagdalena.tiendauni.DTOs;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class CategoryDTOs {
    public record CreateCategoryRequest(
            @NotBlank(message = "El nombre de la categoría no puede estar vacío")
            String name,
            @NotBlank(message = "La descripción no puede estar vacía")
            String description
    ) implements Serializable {}

    public record CategoryResponse (
            Long id,
            String name,
            String description
    ) implements Serializable {}
}