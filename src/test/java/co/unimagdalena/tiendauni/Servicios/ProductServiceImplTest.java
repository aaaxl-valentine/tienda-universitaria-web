package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.ProductDTOs.CreateProductRequest;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.ProductResponse;
import co.unimagdalena.tiendauni.DTOs.ProductDTOs.UpdateProductRequest;
import co.unimagdalena.tiendauni.entity.Category;
import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.entity.OrderItem;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.enums.OrderStatus;
import co.unimagdalena.tiendauni.repository.CategoryRepository;
import co.unimagdalena.tiendauni.repository.OrderItemRepository;
import co.unimagdalena.tiendauni.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void shouldRejectCreateProductWhenSkuAlreadyExists() {
        CreateProductRequest request = new CreateProductRequest(
                "SKU-1", "Cuaderno", "200 hojas", new BigDecimal("12.00"), true, 5L
        );
        when(productRepository.existsBySku("SKU-1")).thenReturn(true);

        assertThatThrownBy(() -> service.createProduct(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldCreateProductWithInventory() {
        Category category = Category.builder().id(5L).name("Utiles").build();
        CreateProductRequest request = new CreateProductRequest(
                "SKU-2", "Borrador", "Blanco", new BigDecimal("2.50"), true, 5L
        );

        when(productRepository.existsBySku("SKU-2")).thenReturn(false);
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(22L);
            return product;
        });
        when(productRepository.findById(22L)).thenReturn(Optional.of(
                Product.builder()
                        .id(22L)
                        .sku("SKU-2")
                        .name("Borrador")
                        .description("Blanco")
                        .price(new BigDecimal("2.50"))
                        .active(true)
                        .category(category)
                        .build()
        ));

        ProductResponse response = service.createProductWithInventory(request, 30, 10);

        assertThat(response.id()).isEqualTo(22L);
        assertThat(response.sku()).isEqualTo("SKU-2");
        verify(inventoryService).createInventoryForProduct(22L, 30, 10);
    }

    @Test
    void shouldRejectDeactivationWhenProductHasActiveOrders() {
        Product product = Product.builder().id(10L).sku("SKU-10").active(true).build();
        Order order = Order.builder().id(99L).status(OrderStatus.CREATED).build();
        OrderItem orderItem = OrderItem.builder().order(order).build();

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderItemRepository.findByProductId(10L)).thenReturn(List.of(orderItem));

        assertThatThrownBy(() -> service.setProductActive(10L, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("active orders");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldRejectUpdateWithInvalidPrice() {
        Product product = Product.builder().id(10L).sku("SKU-10").name("Regla").price(new BigDecimal("5.00")).active(true).build();
        UpdateProductRequest request = new UpdateProductRequest("Regla", "30cm", BigDecimal.ZERO, true, null);

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.updateProduct(10L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }
}
