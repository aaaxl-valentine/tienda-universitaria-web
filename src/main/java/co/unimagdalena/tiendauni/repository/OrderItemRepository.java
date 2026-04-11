package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    // Productos más vendidos con cantidades
    @Query("SELECT oi.product, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED') " +
           "AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY oi.product " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProductsWithQuantities(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
