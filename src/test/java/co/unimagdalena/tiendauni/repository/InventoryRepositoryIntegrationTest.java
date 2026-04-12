package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Category;
import co.unimagdalena.tiendauni.entity.Inventory;
import co.unimagdalena.tiendauni.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryRepositoryIntegrationTest extends AbstractRepositoryIT {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void buscarProductosConBajoStock() {
        Category categoria = categoryRepository.save(Category.builder()
                .name("Papeleria")
                .description("Papeleria")
                .build());

        Product productoBajo = productRepository.save(Product.builder()
                .sku("SKU-INV-001")
                .name("Resma A4")
                .description("Resma")
                .price(new BigDecimal("15.00"))
                .category(categoria)
                .build());

        Product productoCritico = productRepository.save(Product.builder()
                .sku("SKU-INV-002")
                .name("Marcador")
                .description("Marcador")
                .price(new BigDecimal("2.00"))
                .category(categoria)
                .build());

        Product productoEstable = productRepository.save(Product.builder()
                .sku("SKU-INV-003")
                .name("Carpeta")
                .description("Carpeta")
                .price(new BigDecimal("8.00"))
                .category(categoria)
                .build());

        Inventory bajo = inventoryRepository.save(Inventory.builder()
                .product(productoBajo)
                .availableStock(3)
                .minimumStock(5)
                .build());

        Inventory critico = inventoryRepository.save(Inventory.builder()
                .product(productoCritico)
                .availableStock(1)
                .minimumStock(4)
                .build());

        inventoryRepository.save(Inventory.builder()
                .product(productoEstable)
                .availableStock(20)
                .minimumStock(5)
                .build());

        List<Inventory> bajos = inventoryRepository.findLowStockInventories();
        List<Inventory> ordenados = inventoryRepository.findProductsWithInsufficientStock();

        assertThat(bajos).hasSize(2);
        assertThat(bajos).extracting(i -> i.getProduct().getSku())
                .containsExactlyInAnyOrder("SKU-INV-001", "SKU-INV-002");

        assertThat(ordenados).hasSize(2);
        assertThat(ordenados.get(0).getId()).isEqualTo(critico.getId());
        assertThat(ordenados.get(1).getId()).isEqualTo(bajo.getId());
    }
}
