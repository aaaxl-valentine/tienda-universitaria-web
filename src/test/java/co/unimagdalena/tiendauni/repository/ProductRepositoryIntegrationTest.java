package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Address;
import co.unimagdalena.tiendauni.entity.Category;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.entity.OrderItem;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryIntegrationTest extends AbstractRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void buscarProductosPorCategoriaActiva() {
        Category libros = categoryRepository.save(Category.builder()
                .name("Libros")
                .description("Libros universitarios")
                .build());
        Category tecnologia = categoryRepository.save(Category.builder()
                .name("Tecnologia")
                .description("Articulos de tecnologia")
                .build());

        Product productoActivo = productRepository.save(Product.builder()
                .sku("SKU-LIB-001")
                .name("Libro de Calculo")
                .description("Libro")
                .price(new BigDecimal("100.00"))
                .active(true)
                .category(libros)
                .build());

        productRepository.save(Product.builder()
                .sku("SKU-LIB-002")
                .name("Libro Inactivo")
                .description("Libro")
                .price(new BigDecimal("150.00"))
                .active(false)
                .category(libros)
                .build());

        productRepository.save(Product.builder()
                .sku("SKU-TEC-001")
                .name("Mouse")
                .description("Mouse")
                .price(new BigDecimal("50.00"))
                .active(true)
                .category(tecnologia)
                .build());

        List<Product> encontrados = productRepository.findByCategoryIdAndActiveTrue(libros.getId());

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getId()).isEqualTo(productoActivo.getId());
        assertThat(encontrados.get(0).getSku()).isEqualTo("SKU-LIB-001");
    }

    @Test
    void topProductosVendidosPorPeriodo() {
        Category categoria = categoryRepository.save(Category.builder()
                .name("Accesorios")
                .description("Accesorios")
                .build());

        Product productoTop = productRepository.save(Product.builder()
                .sku("SKU-TOP-001")
                .name("Cuaderno")
                .description("Cuaderno")
                .price(new BigDecimal("20.00"))
                .category(categoria)
                .build());

        Product productoSecundario = productRepository.save(Product.builder()
                .sku("SKU-TOP-002")
                .name("Lapicero")
                .description("Lapicero")
                .price(new BigDecimal("5.00"))
                .category(categoria)
                .build());

        Customer customer = customerRepository.save(Customer.builder()
                .firstName("Ana")
                .lastName("Perez")
                .email("ana.perez@test.com")
                .build());

        Address address = addressRepository.save(Address.builder()
                .street("Calle 1")
                .city("Santa Marta")
                .department("Magdalena")
                .postalCode("470001")
                .isDefault(true)
                .customer(customer)
                .build());

        Order ordenPagada = orderRepository.save(Order.builder()
                .customer(customer)
                .address(address)
                .status(OrderStatus.PAID)
                .total(new BigDecimal("100.00"))
                .build());
        actualizarFechaPedido(ordenPagada.getId(), LocalDateTime.of(2026, 1, 5, 10, 0));

        Order ordenCancelada = orderRepository.save(Order.builder()
                .customer(customer)
                .address(address)
                .status(OrderStatus.CANCELLED)
                .total(new BigDecimal("60.00"))
                .build());
        actualizarFechaPedido(ordenCancelada.getId(), LocalDateTime.of(2026, 1, 8, 10, 0));

        orderItemRepository.save(OrderItem.builder()
                .order(ordenPagada)
                .product(productoTop)
                .quantity(5)
                .unitPrice(new BigDecimal("20.00"))
                .subtotal(new BigDecimal("100.00"))
                .build());

        orderItemRepository.save(OrderItem.builder()
                .order(ordenPagada)
                .product(productoSecundario)
                .quantity(2)
                .unitPrice(new BigDecimal("5.00"))
                .subtotal(new BigDecimal("10.00"))
                .build());

        orderItemRepository.save(OrderItem.builder()
                .order(ordenCancelada)
                .product(productoSecundario)
                .quantity(50)
                .unitPrice(new BigDecimal("5.00"))
                .subtotal(new BigDecimal("250.00"))
                .build());

        List<Product> topProductos = productRepository.findTopSellingProductsByPeriod(
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 31, 23, 59));

        assertThat(topProductos).hasSize(2);
        assertThat(topProductos.get(0).getId()).isEqualTo(productoTop.getId());
        assertThat(topProductos).extracting(Product::getId)
                .containsExactlyInAnyOrder(productoTop.getId(), productoSecundario.getId());
    }

    private void actualizarFechaPedido(Long orderId, LocalDateTime createdAt) {
        entityManager.createNativeQuery("UPDATE orders SET created_at = :createdAt WHERE id = :orderId")
                .setParameter("createdAt", Timestamp.valueOf(createdAt))
                .setParameter("orderId", orderId)
                .executeUpdate();
        entityManager.clear();
    }
}
