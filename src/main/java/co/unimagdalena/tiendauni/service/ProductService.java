package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.ProductDTOs.CreateProductRequest;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.ProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.UpdateProductRequest;


import java.util.List;
import java.util.Optional;

public interface ProductService {

    /**
     * Crea un nuevo producto con validaciones de negocio
     */
    ProductResponse createProduct(CreateProductRequest request);

    /**
     * Crea un nuevo producto con inventario inicial
     */
    ProductResponse createProductWithInventory(CreateProductRequest request, Integer initialStock, Integer minimumStock);

    /**
     * Busca un producto por ID
     */
    Optional<ProductResponse> findById(Long id);

    /**
     * Busca un producto por SKU
     */
    Optional<ProductResponse> findBySku(String sku);

    /**
     * Verifica si existe un producto con el SKU dado
     */
    boolean existsBySku(String sku);

    /**
     * Actualiza un producto existente con validaciones
     */
    ProductResponse updateProduct(Long productId, UpdateProductRequest request);

    /**
     * Activa/desactiva un producto con validaciones de negocio
     */
    ProductResponse setProductActive(Long productId, boolean active);

    /**
     * Obtiene todos los productos
     */
    List<ProductResponse> findAll();

    /**
     * Obtiene productos por categoría
     */
    List<ProductResponse> findByCategory(Long categoryId);

    /**
     * Obtiene productos activos
     */
    List<ProductResponse> findActiveProducts();
}