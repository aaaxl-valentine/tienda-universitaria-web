package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.CategoryDTOs.*;
import co.unimagdalena.tiendauni.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Validated
                                                       @RequestBody CreateCategoryRequest request,
                                                   UriComponentsBuilder uriBuilder){
        var categoryCreated = categoryService.create(request);

        var location =  uriBuilder.path("/api/categories/{id}").buildAndExpand(categoryCreated.id()).toUri();
        return ResponseEntity.created(location).body(categoryCreated);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable long id) {
        return ResponseEntity.ok(categoryService.get(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable long id,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
