package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.ProductDTOs.CreateProductRequest;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.ProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.UpdateProductRequest;
import co.unimagdalena.tiendauni.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest req,
                                                  UriComponentsBuilder uriBuilder) {
        var productCreated = productService.createProduct(req);
        var location = uriBuilder.path("/api/products/{id}").buildAndExpand(productCreated.id()).toUri();
        return ResponseEntity.created(location).body(productCreated);
    }

    @PostMapping("/with-inventory")
    public ResponseEntity<ProductResponse> createProductWithInventory(@Valid @RequestBody CreateProductRequest req,
                                                               @RequestParam Integer initialStock,
                                                               @RequestParam Integer minimumStock,
                                                               UriComponentsBuilder uriBuilder) {
        var productCreated = productService.createProductWithInventory(req, initialStock, minimumStock);
        var location = uriBuilder.path("/api/products/{id}").buildAndExpand(productCreated.id()).toUri();
        return ResponseEntity.created(location).body(productCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        return productService.findBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        return ResponseEntity.ok(productService.findActiveProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable long categoryId) {
        return ResponseEntity.ok(productService.findByCategory(categoryId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable long id,
                                                  @Valid @RequestBody UpdateProductRequest req) {
        return ResponseEntity.ok(productService.updateProduct(id, req));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ProductResponse> setProductActive(@PathVariable long id,
                                                     @RequestParam boolean active) {
        return ResponseEntity.ok(productService.setProductActive(id, active));
    }
}
