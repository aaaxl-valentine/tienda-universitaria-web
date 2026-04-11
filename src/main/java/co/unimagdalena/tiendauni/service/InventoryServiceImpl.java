package co.unimagdalena.tiendauni.service;

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
    public Optional<Inventory> findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Override
    public Optional<Inventory> findByProductSku(String sku) {
        return inventoryRepository.findByProductSku(sku);
    }

    @Override
    public List<Inventory> getLowStockInventories() {
        return inventoryRepository.findLowStockInventories();
    }

    @Override
    @Transactional
    public Inventory createInventoryForProduct(Long productId, Integer initialStock, Integer minimumStock) {
        // Validar valores de stock
        validateStockValues(initialStock, minimumStock);

        // Verificar que el producto existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));

        // Verificar que no exista ya inventario para este producto
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for product ID: " + productId);
        }

        Inventory inventory = Inventory.builder()
                .availableStock(initialStock)
                .minimumStock(minimumStock)
                .product(product)
                .build();

        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory updateAvailableStock(Long productId, Integer newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + productId));

        inventory.setAvailableStock(newStock);
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory updateMinimumStock(Long productId, Integer newMinimumStock) {
        if (newMinimumStock < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + productId));

        inventory.setMinimumStock(newMinimumStock);
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory incrementStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + productId));

        inventory.setAvailableStock(inventory.getAvailableStock() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory decrementStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + productId));

        int newStock = inventory.getAvailableStock() - quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + inventory.getAvailableStock() + ", requested: " + quantity);
        }

        inventory.setAvailableStock(newStock);
        return inventoryRepository.save(inventory);
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
}