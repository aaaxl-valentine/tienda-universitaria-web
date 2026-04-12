package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CancelOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CreateOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.entity.Order;
import co.unimagdalena.tiendauni.entity.OrderStatusHistory;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    /**
     * Crea un nuevo pedido con todas las validaciones de negocio
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Busca un pedido por ID
     */
    Optional<OrderResponse> findById(Long id);

    /**
     * Busca todos los pedidos de un cliente
     */
    List<OrderResponse> findByCustomerId(Long customerId);

    /**
     * Obtiene el total de un pedido (suma de subtotales de ítems)
     */
    java.math.BigDecimal calculateOrderTotal(Long orderId);

    /**
     * Procesa el pago del pedido (CREATED → PAID)
     * Valida stock y descuenta inventario
     */
    OrderResponse processPayment(Long orderId);

    /**
     * Despacha un pedido (PAID → SHIPPED)
     */
    OrderResponse shipOrder(Long orderId);

    /**
     * Marca un pedido como entregado (SHIPPED → DELIVERED)
     */
    OrderResponse deliverOrder(Long orderId);

    /**
     * Cancela un pedido según reglas de estado y reversión de stock
     */
    OrderResponse cancelOrder(CancelOrderRequest request);

    /**
     * Obtiene el historial de cambios de estado de un pedido
     */
    java.util.List<OrderStatusHistory> getOrderHistory(Long orderId);
}