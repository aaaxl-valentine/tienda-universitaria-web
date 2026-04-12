package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.CategoryDTOs.*;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.repository.CategoryRepository;
import co.unimagdalena.tiendauni.service.mappers.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        return  CategoryMapper.toResponse(categoryRepository.save(CategoryMapper.toEntity(request)));

    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse get(Long id) {
        return categoryRepository.findById(id).map(CategoryMapper::toResponse).orElseThrow(
                () -> new ResourceNotFoundException("Categoria %d no encontrada")
        );
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(CategoryMapper::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
