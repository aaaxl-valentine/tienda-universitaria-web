package co.unimagdalena.tiendauni.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import co.unimagdalena.tiendauni.entity.enums.OrderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El estado que se registró en este momento
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // Nota opcional: ej. "Cancelado por stock insuficiente"
    @Column(length = 300)
    private String notes;

    @Column(name = "changed_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime changedAt;

    // Este registro pertenece a un pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}