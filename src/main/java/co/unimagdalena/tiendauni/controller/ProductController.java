package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.ProductDTOs.CreateProductRequest;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.ProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.UpdateProductRequest;
import co.unimagdalena.tiendauni.service.InventoryService;
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
    private final InventoryService inventoryService;

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
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {

        return ResponseEntity.ok(productService.findBySku(sku));
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

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> replaceProduct(@PathVariable long id,
                                                    @Valid @RequestBody UpdateProductRequest req) {
        return ResponseEntity.ok(productService.updateProduct(id, req));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ProductResponse> setProductActive(@PathVariable long id,
                                                     @RequestParam boolean active) {
        return ResponseEntity.ok(productService.setProductActive(id, active));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/inventory")
    public ResponseEntity<ProductResponse> updateProductInventory(
            @PathVariable long id,
            @RequestParam Integer initialStock,
            @RequestParam Integer minimumStock
    ) {
        inventoryService.createInventoryForProduct(id, initialStock, minimumStock);
        return ResponseEntity.ok(productService.findById(id));
    }

}
