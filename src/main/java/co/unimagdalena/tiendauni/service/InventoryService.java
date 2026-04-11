package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.entity.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryService {

    /**
     * Busca el inventario de un producto por su ID
     */
    Optional<Inventory> findByProductId(Long productId);

    /**
     * Busca el inventario de un producto por su SKU
     */
    Optional<Inventory> findByProductSku(String sku);

    /**
     * Obtiene todos los inventarios con stock bajo (availableStock < minimumStock)
     */
    List<Inventory> getLowStockInventories();

    /**
     * Crea inventario inicial para un producto
     */
    Inventory createInventoryForProduct(Long productId, Integer initialStock, Integer minimumStock);

    /**
     * Actualiza el stock disponible de un producto
     */
    Inventory updateAvailableStock(Long productId, Integer newStock);

    /**
     * Actualiza el stock mínimo de un producto
     */
    Inventory updateMinimumStock(Long productId, Integer newMinimumStock);

    /**
     * Incrementa el stock disponible
     */
    Inventory incrementStock(Long productId, Integer quantity);

    /**
     * Decrementa el stock disponible (con validación de no ir por debajo de 0)
     */
    Inventory decrementStock(Long productId, Integer quantity);

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