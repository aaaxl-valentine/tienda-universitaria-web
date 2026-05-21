package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.CategoryDTOs.*;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CreateCategoryRequest request);
    CategoryResponse get(Long id);
    CategoryResponse update(Long id, CreateCategoryRequest request);
    List<CategoryResponse> getAll();
    void delete(Long id);
}
