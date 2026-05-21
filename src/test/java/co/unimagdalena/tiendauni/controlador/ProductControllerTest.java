package co.unimagdalena.tiendauni.controlador;

import co.unimagdalena.tiendauni.DTOs.ProductDTOs.CreateProductRequest;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.ProductResponse;
import co.unimagdalena.tiendauni.NotFoundException.GlobalExceptionHandler;
import co.unimagdalena.tiendauni.controller.ProductController;
import co.unimagdalena.tiendauni.service.InventoryService;
import co.unimagdalena.tiendauni.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    void createProductReturnsCreated() throws Exception {
        var request = new CreateProductRequest("SKU-1", "Cuaderno", "Cuaderno norma", new BigDecimal("10.0"), true, 1L);
        var response = new ProductResponse(1L, "SKU-1", "Cuaderno", "Cuaderno norma", new BigDecimal("10.0"), true, LocalDateTime.now(), 1L);
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createProductWithInvalidNameReturnsBadRequest() throws Exception {
        var request = new CreateProductRequest("SKU-2", "", "Cuaderno", new BigDecimal("10.0"), true, 1L);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
