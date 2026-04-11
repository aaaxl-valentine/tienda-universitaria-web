package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    // Historial de cambios de un pedido
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(Long orderId);

    // Historial de cambios por período
    @Query("SELECT h FROM OrderStatusHistory h WHERE h.order.id = :orderId " +
           "AND h.changedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY h.changedAt ASC")
    List<OrderStatusHistory> findOrderHistoryByPeriod(
            @Param("orderId") Long orderId,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);
}