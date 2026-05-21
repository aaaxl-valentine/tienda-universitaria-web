package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CancelOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CreateOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.entity.OrderStatusHistory;

import java.util.List;

public interface OrderService {

    /**
     * Crea un nuevo pedido con todas las validaciones de negocio
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Obtiene todos los pedidos
     */
    List<OrderResponse> findAll();

    /**
     * Busca un pedido por ID
     */
    OrderResponse findById(Long id);

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
     * Cancela un pedido por ID con razón por defecto
     */
    OrderResponse cancelOrder(Long orderId);

    /**
     * Cancela un pedido según reglas de estado y reversión de stock
     */
    OrderResponse cancelOrder(CancelOrderRequest request);

    /**
     * Obtiene el historial de cambios de estado de un pedido
     */
    java.util.List<OrderStatusHistory> getOrderHistory(Long orderId);

    /**
     * Elimina un pedido por ID
     */
    void deleteOrder(Long orderId);
}
