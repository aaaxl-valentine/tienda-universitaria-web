package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CancelOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CreateOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.DTOs.OrderItemDTOs.CreateOrderItemRequest;
import co.unimagdalena.tiendauni.entity.Address;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.entity.OrderItem;
import co.unimagdalena.tiendauni.entity.OrderStatusHistory;
import co.unimagdalena.tiendauni.entity.Product;
import co.unimagdalena.tiendauni.enums.CustomerStatus;
import co.unimagdalena.tiendauni.enums.OrderStatus;
import co.unimagdalena.tiendauni.repository.AddressRepository;
import co.unimagdalena.tiendauni.repository.CustomerRepository;
import co.unimagdalena.tiendauni.repository.OrderItemRepository;
import co.unimagdalena.tiendauni.repository.OrderRepository;
import co.unimagdalena.tiendauni.repository.OrderStatusHistoryRepository;
import co.unimagdalena.tiendauni.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @InjectMocks
    private OrderServiceImpl service;

    @Test
    void shouldNotCreateOrderWithoutItems() {
        Customer customer = activeCustomer(1L);
        Address address = addressForCustomer(10L, customer);
        CreateOrderRequest request = new CreateOrderRequest(null, 1L, 10L, List.of());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        assertThatThrownBy(() -> service.createOrder(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one item");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldNotCreateOrderWithInvalidQuantity() {
        Customer customer = activeCustomer(1L);
        Address address = addressForCustomer(10L, customer);
        CreateOrderItemRequest badItem = new CreateOrderItemRequest(0, null, null, 100L);
        CreateOrderRequest request = new CreateOrderRequest(null, 1L, 10L, List.of(badItem));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        assertThatThrownBy(() -> service.createOrder(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");

        verify(productRepository, never()).findById(any(Long.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldCalculateSubtotalAndTotalCorrectlyOnCreateOrder() {
        Customer customer = activeCustomer(1L);
        Address address = addressForCustomer(10L, customer);
        Product productA = product(100L, "SKU-100", new BigDecimal("100.50"), true);
        Product productB = product(200L, "SKU-200", new BigDecimal("10.00"), true);

        CreateOrderItemRequest itemA = new CreateOrderItemRequest(2, null, null, 100L);
        CreateOrderItemRequest itemB = new CreateOrderItemRequest(3, null, null, 200L);
        CreateOrderRequest request = new CreateOrderRequest(null, 1L, 10L, List.of(itemA, itemB));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));
        when(productRepository.findById(100L)).thenReturn(Optional.of(productA));
        when(productRepository.findById(200L)).thenReturn(Optional.of(productB));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(999L);
            return order;
        });

        OrderResponse response = service.createOrder(request);

        assertThat(response.total()).isEqualByComparingTo(new BigDecimal("231.00"));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.getItems().get(0).getSubtotal()).isEqualByComparingTo(new BigDecimal("201.00"));
        assertThat(savedOrder.getItems().get(1).getSubtotal()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(savedOrder.getTotal()).isEqualByComparingTo(new BigDecimal("231.00"));

        ArgumentCaptor<OrderStatusHistory> historyCaptor = ArgumentCaptor.forClass(OrderStatusHistory.class);
        verify(orderStatusHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldRejectPaymentWhenStockIsInsufficient() {
        Product product = product(100L, "SKU-100", new BigDecimal("10.00"), true);
        Order order = orderWithStatusAndItems(50L, OrderStatus.CREATED, List.of(orderItem(5, product, new BigDecimal("10.00"))));

        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));
        when(inventoryService.getAvailableStock(100L)).thenReturn(2);

        assertThatThrownBy(() -> service.processPayment(50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");

        verify(inventoryService, never()).decrementStock(any(Long.class), any(Integer.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldDecrementInventoryWhenProcessingPayment() {
        Product productA = product(100L, "SKU-100", new BigDecimal("10.00"), true);
        Product productB = product(200L, "SKU-200", new BigDecimal("20.00"), true);
        Order order = orderWithStatusAndItems(
                50L,
                OrderStatus.CREATED,
                List.of(
                        orderItem(2, productA, new BigDecimal("10.00")),
                        orderItem(3, productB, new BigDecimal("20.00"))
                )
        );

        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));
        when(inventoryService.getAvailableStock(100L)).thenReturn(10);
        when(inventoryService.getAvailableStock(200L)).thenReturn(10);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = service.processPayment(50L);

        assertThat(response.status()).isEqualTo(OrderStatus.PAID);
        verify(inventoryService).decrementStock(100L, 2);
        verify(inventoryService).decrementStock(200L, 3);

        ArgumentCaptor<OrderStatusHistory> historyCaptor = ArgumentCaptor.forClass(OrderStatusHistory.class);
        verify(orderStatusHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldRevertStockWhenCancellingPaidOrder() {
        Product productA = product(100L, "SKU-100", new BigDecimal("10.00"), true);
        Product productB = product(200L, "SKU-200", new BigDecimal("20.00"), true);
        Order paidOrder = orderWithStatusAndItems(
                80L,
                OrderStatus.PAID,
                List.of(
                        orderItem(1, productA, new BigDecimal("10.00")),
                        orderItem(4, productB, new BigDecimal("20.00"))
                )
        );

        when(orderRepository.findById(80L)).thenReturn(Optional.of(paidOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = service.cancelOrder(new CancelOrderRequest(80L, "Cliente solicitó cancelación"));

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
        verify(inventoryService).incrementStock(100L, 1);
        verify(inventoryService).incrementStock(200L, 4);

        ArgumentCaptor<OrderStatusHistory> historyCaptor = ArgumentCaptor.forClass(OrderStatusHistory.class);
        verify(orderStatusHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(historyCaptor.getValue().getNotes()).isEqualTo("Cliente solicitó cancelación");
    }

    @Test
    void shouldValidateStateTransitionWhenShippingOrder() {
        Order createdOrder = orderWithStatusAndItems(33L, OrderStatus.CREATED, List.of());
        when(orderRepository.findById(33L)).thenReturn(Optional.of(createdOrder));

        assertThatThrownBy(() -> service.shipOrder(33L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only orders in PAID status can be shipped");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldValidateStateTransitionWhenDeliveringOrder() {
        Order paidOrder = orderWithStatusAndItems(34L, OrderStatus.PAID, List.of());
        when(orderRepository.findById(34L)).thenReturn(Optional.of(paidOrder));

        assertThatThrownBy(() -> service.deliverOrder(34L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only orders in SHIPPED status can be delivered");

        verify(orderRepository, never()).save(any(Order.class));
    }

    private Customer activeCustomer(Long id) {
        return Customer.builder()
                .id(id)
                .firstName("Ana")
                .lastName("Ruiz")
                .email("ana@example.com")
                .status(CustomerStatus.ACTIVE)
                .build();
    }

    private Address addressForCustomer(Long id, Customer customer) {
        return Address.builder()
                .id(id)
                .street("Calle 1")
                .city("Santa Marta")
                .customer(customer)
                .build();
    }

    private Product product(Long id, String sku, BigDecimal price, boolean active) {
        return Product.builder()
                .id(id)
                .sku(sku)
                .name("Producto")
                .price(price)
                .active(active)
                .build();
    }

    private Order orderWithStatusAndItems(Long id, OrderStatus status, List<OrderItem> items) {
        Order order = Order.builder()
                .id(id)
                .status(status)
                .total(BigDecimal.ZERO)
                .build();
        order.setItems(items);
        items.forEach(item -> item.setOrder(order));
        return order;
    }

    private OrderItem orderItem(Integer quantity, Product product, BigDecimal unitPrice) {
        return OrderItem.builder()
                .quantity(quantity)
                .product(product)
                .unitPrice(unitPrice)
                .subtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .build();
    }
}
