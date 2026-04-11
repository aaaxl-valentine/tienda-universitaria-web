package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryId(Long categoryId);

    // Buscar productos activos por categoría
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    // Productos más vendidos por período
    @Query("SELECT p FROM Product p WHERE p.id IN (" +
           "SELECT oi.product.id FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED') " +
           "AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY oi.product.id " +
           "ORDER BY SUM(oi.quantity) DESC)")
    List<Product> findTopSellingProductsByPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Productos con stock insuficiente (esto ya está en InventoryRepository)
    // pero aquí podríamos tener una versión que incluya el producto
}
