package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.InventoryDTOs.InventoryResponse;
import co.unimagdalena.tiendauni.DTOs.InventoryDTOs.UpdateInventoryRequest;
import co.unimagdalena.tiendauni.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable long productId) {
        return ResponseEntity.ok(inventoryService.findByProductId(productId)
                .orElseThrow(() -> new co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException(
                        "Inventario para producto " + productId + " no encontrado")));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<InventoryResponse> getInventoryBySku(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.findByProductSku(sku)
                .orElseThrow(() -> new co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException(
                        "Inventario para SKU " + sku + " no encontrado")));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockInventories() {
        return ResponseEntity.ok(inventoryService.getLowStockInventories());
    }

    @PostMapping("/{productId}")
    public ResponseEntity<InventoryResponse> createInventory(
            @PathVariable long productId,
            @RequestParam Integer initialStock,
            @RequestParam Integer minimumStock
    ) {
        return ResponseEntity.ok(inventoryService.createInventoryForProduct(productId, initialStock, minimumStock));
    }

    @PatchMapping("/product/{productId}/stock")
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable long productId,
            @RequestParam Integer newStock
    ) {
        return ResponseEntity.ok(inventoryService.updateAvailableStock(productId, newStock));
    }

    @PatchMapping("/product/{productId}/minimum")
    public ResponseEntity<InventoryResponse> updateMinimumStock(
            @PathVariable long productId,
            @RequestParam Integer newMinimumStock
    ) {
        return ResponseEntity.ok(inventoryService.updateMinimumStock(productId, newMinimumStock));
    }

    @PostMapping("/product/{productId}/increment")
    public ResponseEntity<InventoryResponse> incrementStock(
            @PathVariable long productId,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(inventoryService.incrementStock(productId, quantity));
    }

    @PostMapping("/product/{productId}/decrement")
    public ResponseEntity<InventoryResponse> decrementStock(
            @PathVariable long productId,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(inventoryService.decrementStock(productId, quantity));
    }

    @GetMapping("/product/{productId}/is-low-stock")
    public ResponseEntity<Boolean> isLowStock(@PathVariable long productId) {
        return ResponseEntity.ok(inventoryService.isLowStock(productId));
    }

    @GetMapping("/product/{productId}/available")
    public ResponseEntity<Integer> getAvailableStock(@PathVariable long productId) {
        return ResponseEntity.ok(inventoryService.getAvailableStock(productId));
    }
}
