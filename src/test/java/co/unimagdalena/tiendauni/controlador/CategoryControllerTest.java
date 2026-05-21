package co.unimagdalena.tiendauni.controlador;

import co.unimagdalena.tiendauni.DTOs.CategoryDTOs.*;
import co.unimagdalena.tiendauni.controller.CategoryController;
import co.unimagdalena.tiendauni.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void createCategory_ShouldReturnCreated() {
        var request = new CreateCategoryRequest("Libros", "Libros universitarios");
        var response = new CategoryResponse(1L, "Libros", "Libros universitarios");

        when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(response);

        var result = categoryController.create(request, UriComponentsBuilder.newInstance());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getCategories_ShouldReturnList() {
        var response1 = new CategoryResponse(1L, "Libros", "Libros universitarios");
        var response2 = new CategoryResponse(2L, "Tecnologia", "Articulos de tecnologia");

        when(categoryService.getAll()).thenReturn(List.of(response1, response2));

        var result = categoryController.getCategories();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody().get(0).id()).isEqualTo(1L);
        assertThat(result.getBody().get(1).id()).isEqualTo(2L);
    }

    @Test
    void getCategoryById_ShouldReturnOk() {
        var response = new CategoryResponse(1L, "Libros", "Libros universitarios");

        when(categoryService.get(1L)).thenReturn(response);

        var result = categoryController.getCategoryById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updateCategory_ShouldReturnOk() {
        var request = new CreateCategoryRequest("Libros Actualizados", "Descripcion actualizada");
        var response = new CategoryResponse(1L, "Libros Actualizados", "Descripcion actualizada");

        when(categoryService.update(eq(1L), any(CreateCategoryRequest.class))).thenReturn(response);

        var result = categoryController.updateCategory(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        verify(categoryService).update(1L, request);
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() {
        var result = categoryController.deleteCategory(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(categoryService).delete(1L);
    }
}
