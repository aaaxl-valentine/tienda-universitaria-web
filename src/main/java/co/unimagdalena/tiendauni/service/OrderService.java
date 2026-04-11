package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.entity.OrderStatusHistory;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    /**
     * DTO para crear un ítem de pedido (request)
     */
    record CreateOrderItemRequest(Long productId, Integer quantity) {}

    /**
     * DTO para crear un pedido (request)
     */
    record CreateOrderRequest(
            Long customerId,
            Long addressId,
            List<CreateOrderItemRequest> items
    ) {}

    /**
     * Crea un nuevo pedido con todas las validaciones de negocio
     */
    Order createOrder(CreateOrderRequest request);

    /**
     * Busca un pedido por ID
     */
    Optional<Order> findById(Long id);

    /**
     * Busca todos los pedidos de un cliente
     */
    List<Order> findByCustomerId(Long customerId);

    /**
     * Obtiene el total de un pedido (suma de subtotales de ítems)
     */
    java.math.BigDecimal calculateOrderTotal(Long orderId);

    /**
     * Procesa el pago del pedido (CREATED → PAID)
     * Valida stock y descuenta inventario
     */
    Order processPayment(Long orderId);

    /**
     * Despacha un pedido (PAID → SHIPPED)
     */
    Order shipOrder(Long orderId);

    /**
     * Marca un pedido como entregado (SHIPPED → DELIVERED)
     */
    Order deliverOrder(Long orderId);

    /**
     * Cancela un pedido según reglas de estado y reversión de stock
     */
    Order cancelOrder(Long orderId);

    /**
     * Obtiene el historial de cambios de estado de un pedido
     */
    java.util.List<OrderStatusHistory> getOrderHistory(Long orderId);
}