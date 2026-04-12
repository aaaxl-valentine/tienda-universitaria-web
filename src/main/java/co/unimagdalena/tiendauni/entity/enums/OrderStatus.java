package co.unimagdalena.tiendauni.enums;

public enum OrderStatus {

    CREATED,    // Pedido recién creado, aún no pagado
    PAID,       // Pago confirmado, inventario descontado
    SHIPPED,    // Pedido despachado
    DELIVERED, // Pedido entregado al cliente
    CANCELLED  // Pedido cancelado (puede revertir stock)
}
