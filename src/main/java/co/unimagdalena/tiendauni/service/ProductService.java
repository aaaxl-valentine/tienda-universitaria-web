package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.entity.Product;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    /**
     * Crea un nuevo producto con validaciones de negocio
     */
    Product createProduct(String sku, String name, String description, BigDecimal price, Long categoryId);

    /**
     * Crea un nuevo producto con inventario inicial
     */
    Product createProductWithInventory(String sku, String name, String description, BigDecimal price,
                                     Long categoryId, Integer initialStock, Integer minimumStock);

    /**
     * Busca un producto por ID
     */
    Optional<Product> findById(Long id);

    /**
     * Busca un producto por SKU
     */
    Optional<Product> findBySku(String sku);

    /**
     * Verifica si existe un producto con el SKU dado
     */
    boolean existsBySku(String sku);

    /**
     * Actualiza un producto existente con validaciones
     */
    Product updateProduct(Long productId, String name, String description, BigDecimal price, Long categoryId);

    /**
     * Activa/desactiva un producto con validaciones de negocio
     */
    Product setProductActive(Long productId, boolean active);

    /**
     * Obtiene todos los productos
     */
    List<Product> findAll();

    /**
     * Obtiene productos por categoría
     */
    List<Product> findByCategory(Long categoryId);

    /**
     * Obtiene productos activos
     */
    List<Product> findActiveProducts();
}