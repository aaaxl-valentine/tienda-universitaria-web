package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CancelOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CreateOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.DTOs.OrderItemDTOs.CreateOrderItemRequest;
import co.unimagdalena.tiendauni.NotFoundException.ConflictException;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.entity.*;
import co.unimagdalena.tiendauni.repository.*;
import co.unimagdalena.tiendauni.entity.enums.CustomerStatus;
import co.unimagdalena.tiendauni.entity.enums.OrderStatus;
import co.unimagdalena.tiendauni.service.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente con ID " + request.customerId() + " no encontrado"));
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new ConflictException("El cliente con ID " + request.customerId() + " no está activo");
        }

        // Validar que la dirección existe
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Dirección con ID " + request.addressId() + " no encontrada"));

        // Validar que la dirección pertenece al cliente
        if (!address.getCustomer().getId().equals(request.customerId())) {
            throw new ConflictException("La dirección no pertenece al cliente");
        }

        // Validar que hay al menos un ítem
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // Crear el pedido con estado inicial CREATED
        Order order = OrderMapper.toEntity(request, customer, address);
        // Crear los ítems del pedido
        BigDecimal orderTotal = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.items()) {
            // Validar cantidad > 0
            if (itemRequest.quantity() == null || itemRequest.quantity() <= 0) {
                throw new IllegalArgumentException("Item quantity must be greater than zero");
            }

            // Validar que el producto existe
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto con ID " + itemRequest.productId() + " no encontrado"));

            // Validar que el producto está activo
            if (!product.getActive()) {
                throw new ConflictException("El producto con ID " + itemRequest.productId() + " no está activo");
            }

            // Crear ítem: precio unitario se toma del producto
            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(new BigDecimal(itemRequest.quantity()));

            OrderItem item = OrderItem.builder()
                    .quantity(itemRequest.quantity())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .order(order)
                    .product(product)
                    .build();

            order.getItems().add(item);
            orderTotal = orderTotal.add(subtotal);
        }

        // Establecer el total calculado
        order.setTotal(orderTotal);

        // Guardar el pedido
        Order savedOrder = orderRepository.save(order);

        // Registrar estado inicial en el historial
        recordStatusChange(savedOrder, OrderStatus.CREATED, "Order created");

        return OrderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse findById(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Orden %d no encontrada ".formatted(id)));
    }

    @Override
    public List<OrderResponse> findByCustomerId(Long customerId) {
        // Validar que el cliente existe
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Cliente con ID " + customerId + " no encontrado");
        }
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    public BigDecimal calculateOrderTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido con ID " + orderId + " no encontrado"));

        return order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public OrderResponse processPayment(Long orderId) {
        // Obtener el pedido
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido con ID " + orderId + " no encontrado"));

        // Validar que el pedido está en estado CREATED
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new ConflictException("El pedido debe estar en estado CREATED para procesar el pago");
        }

        // Validar stock suficiente para todos los ítems ANTES de descontar
        for (OrderItem item : order.getItems()) {
            Integer currentStock = inventoryService.getAvailableStock(item.getProduct().getId());
            if (currentStock < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product '" + item.getProduct().getSku() + "'. " +
                                "Available: " + currentStock + ", Requested: " + item.getQuantity());
            }
        }

        // Si todas las validaciones pasaron, descontar inventario
        for (OrderItem item : order.getItems()) {
            inventoryService.decrementStock(item.getProduct().getId(), item.getQuantity());
        }

        // Cambiar estado a PAID
        order.setStatus(OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(order);

        // Registrar cambio en historial
        recordStatusChange(updatedOrder, OrderStatus.PAID, "Payment processed successfully");

        return OrderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido con ID " + orderId + " no encontrado"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException("No se puede enviar un pedido cancelado");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new ConflictException("Solo se pueden enviar pedidos en estado PAID");
        }

        order.setStatus(OrderStatus.SHIPPED);
        Order updatedOrder = orderRepository.save(order);
        recordStatusChange(updatedOrder, OrderStatus.SHIPPED, "Order shipped");
        return OrderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido con ID " + orderId + " no encontrado"));

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new ConflictException("Solo se pueden entregar pedidos en estado SHIPPED");
        }

        order.setStatus(OrderStatus.DELIVERED);
        Order updatedOrder = orderRepository.save(order);
        recordStatusChange(updatedOrder, OrderStatus.DELIVERED, "Order delivered");
        return OrderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(CancelOrderRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido con ID " + request.orderId() + " no encontrado"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return OrderMapper.toResponse(order);
        }
        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new ConflictException("No se puede cancelar un pedido enviado");
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new ConflictException("No se puede cancelar un pedido entregado");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            for (OrderItem item : order.getItems()) {
                inventoryService.incrementStock(item.getProduct().getId(), item.getQuantity());
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        String notes = request.reason() != null && !request.reason().isBlank()
                ? request.reason()
                : "Order cancelled";
        recordStatusChange(updatedOrder, OrderStatus.CANCELLED, notes);
        return OrderMapper.toResponse(updatedOrder);
    }

    @Override
    public List<OrderStatusHistory> getOrderHistory(Long orderId) {
        // Validar que el pedido existe
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Pedido con ID " + orderId + " no encontrado");
        }
        return orderStatusHistoryRepository.findByOrderIdOrderByChangedAtAsc(orderId);
    }



    private void recordStatusChange(Order order, OrderStatus newStatus, String notes) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .status(newStatus)
                .notes(notes)
                .order(order)
                .build();

        orderStatusHistoryRepository.save(history);
    }
}
