package co.unimagdalena.tiendauni.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stock actual en bodega — no puede ser negativo
    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;

    // Umbral de alerta: si el stock cae por debajo, es "bajo stock"
    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Un inventario pertenece a exactamente un producto
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
}
