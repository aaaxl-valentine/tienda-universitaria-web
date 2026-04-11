package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    // Top de categorías por volumen de ventas
    @Query("SELECT c, SUM(oi.quantity) as totalSold FROM Category c " +
           "JOIN c.products p " +
           "JOIN p.orderItems oi " +
           "JOIN oi.order o " +
           "WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED') " +
           "AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY c " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopCategoriesBySalesVolume(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
