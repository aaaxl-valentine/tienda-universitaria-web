package co.unimagdalena.tiendauni.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String department;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    // Muchas direcciones pertenecen a un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
