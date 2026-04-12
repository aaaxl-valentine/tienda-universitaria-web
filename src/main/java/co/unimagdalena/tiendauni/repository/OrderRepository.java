package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(OrderStatus status);

    // Buscar pedidos por filtros combinados
    @Query("SELECT o FROM Order o WHERE " +
            "o.customer.id = COALESCE(:customerId, o.customer.id) AND " +
            "o.status = COALESCE(:status, o.status) AND " +
            "o.createdAt >= COALESCE(:startDate, o.createdAt) AND " +
            "o.createdAt <= COALESCE(:endDate, o.createdAt) AND " +
            "o.total >= COALESCE(:minTotal, o.total) AND " +
            "o.total <= COALESCE(:maxTotal, o.total)")
    List<Order> findOrdersByFilters(
            @Param("customerId") Long customerId,
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minTotal") BigDecimal minTotal,
            @Param("maxTotal") BigDecimal maxTotal);

    // Ingresos mensuales agrupados
    @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt), SUM(o.total) " +
            "FROM Order o WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED') " +
            "AND o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
            "ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)")
    List<Object[]> findMonthlyRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Clientes con mayor facturación
    @Query("SELECT o.customer, SUM(o.total) as totalSpent " +
            "FROM Order o WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED') " +
            "AND o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY o.customer " +
            "ORDER BY SUM(o.total) DESC")
    List<Object[]> findTopCustomersByRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}