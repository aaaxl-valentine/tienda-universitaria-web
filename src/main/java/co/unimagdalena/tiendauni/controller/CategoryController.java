package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.CategoryDTOs.*;
import co.unimagdalena.tiendauni.service.CategoryService;
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

    private CategoryService categoryService;

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
}
