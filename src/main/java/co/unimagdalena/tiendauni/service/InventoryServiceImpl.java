package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.InventoryDTOs.InventoryResponse;
import co.unimagdalena.tiendauni.NotFoundException.ConflictException;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.entity.Inventory;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.repository.InventoryRepository;
import co.unimagdalena.tiendauni.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Override
    public Optional<InventoryResponse> findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId).map(this::toResponse);
    }

    @Override
    public Optional<InventoryResponse> findByProductSku(String sku) {
        return inventoryRepository.findByProductSku(sku).map(this::toResponse);
    }

    @Override
    public List<InventoryResponse> getLowStockInventories() {
        return inventoryRepository.findLowStockInventories().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponse createInventoryForProduct(Long productId, Integer initialStock, Integer minimumStock) {
        // Validar valores de stock
        validateStockValues(initialStock, minimumStock);

        // Verificar que el producto existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con ID " + productId + " no encontrado"));

        // Verificar que no exista ya inventario para este producto
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            throw new ConflictException("Ya existe inventario para el producto con ID: " + productId);
        }

        Inventory inventory = Inventory.builder()
                .availableStock(initialStock)
                .minimumStock(minimumStock)
                .product(product)
                .build();

        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse updateAvailableStock(Long productId, Integer newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para el producto con ID: " + productId));

        inventory.setAvailableStock(newStock);
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse updateMinimumStock(Long productId, Integer newMinimumStock) {
        if (newMinimumStock < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para el producto con ID: " + productId));

        inventory.setMinimumStock(newMinimumStock);
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse incrementStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para el producto con ID: " + productId));

        inventory.setAvailableStock(inventory.getAvailableStock() + quantity);
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse decrementStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para el producto con ID: " + productId));

        int newStock = inventory.getAvailableStock() - quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + inventory.getAvailableStock() + ", requested: " + quantity);
        }

        inventory.setAvailableStock(newStock);
        return toResponse(inventoryRepository.save(inventory));
    }

    @Override
    public boolean isLowStock(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(inventory -> inventory.getAvailableStock() < inventory.getMinimumStock())
                .orElse(false);
    }

    @Override
    public Integer getAvailableStock(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(Inventory::getAvailableStock)
                .orElse(0);
    }

    @Override
    public void validateStockValues(Integer availableStock, Integer minimumStock) {
        if (availableStock < 0) {
            throw new IllegalArgumentException("Available stock cannot be negative");
        }
        if (minimumStock < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getAvailableStock(),
                inventory.getMinimumStock(),
                inventory.getUpdatedAt(),
                inventory.getProduct() != null ? inventory.getProduct().getId() : null
        );
    }
}
