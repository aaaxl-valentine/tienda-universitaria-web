package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.InventoryDTOs.InventoryResponse;

import java.util.List;
import java.util.Optional;

public interface InventoryService {

    /**
     * Busca el inventario de un producto por su ID
     */
    Optional<InventoryResponse> findByProductId(Long productId);

    /**
     * Busca el inventario de un producto por su SKU
     */
    Optional<InventoryResponse> findByProductSku(String sku);

    /**
     * Obtiene todos los inventarios con stock bajo (availableStock < minimumStock)
     */
    List<InventoryResponse> getLowStockInventories();

    /**
     * Crea inventario inicial para un producto
     */
    InventoryResponse createInventoryForProduct(Long productId, Integer initialStock, Integer minimumStock);

    /**
     * Actualiza el stock disponible de un producto
     */
    InventoryResponse updateAvailableStock(Long productId, Integer newStock);

    /**
     * Actualiza el stock mínimo de un producto
     */
    InventoryResponse updateMinimumStock(Long productId, Integer newMinimumStock);

    /**
     * Incrementa el stock disponible
     */
    InventoryResponse incrementStock(Long productId, Integer quantity);

    /**
     * Decrementa el stock disponible (con validación de no ir por debajo de 0)
     */
    InventoryResponse decrementStock(Long productId, Integer quantity);

    /**
     * Verifica si un producto tiene stock bajo
     */
    boolean isLowStock(Long productId);

    /**
     * Obtiene el stock disponible actual de un producto
     */
    Integer getAvailableStock(Long productId);

    /**
     * Valida que el stock no sea negativo
     */
    void validateStockValues(Integer availableStock, Integer minimumStock);
}