package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    Optional<Inventory> findByProductSku(String sku);

    @Query("select i from Inventory i where i.availableStock < i.minimumStock")
    List<Inventory> findLowStockInventories();

    // Productos con stock insuficiente (detallado)
    @Query("SELECT i FROM Inventory i WHERE i.availableStock < i.minimumStock ORDER BY i.availableStock ASC")
    List<Inventory> findProductsWithInsufficientStock();
}
