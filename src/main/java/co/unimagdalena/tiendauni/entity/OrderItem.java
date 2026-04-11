package co.unimagdalena.tiendauni.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    // Precio al momento de la compra — NO cambia si cambia el producto
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    // subtotal = quantity * unitPrice (calculado por el service)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    // Este ítem pertenece a un pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Este ítem referencia un producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}