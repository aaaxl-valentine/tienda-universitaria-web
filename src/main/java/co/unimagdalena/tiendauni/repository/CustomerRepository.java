package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    // Buscar clientes activos
    List<Customer> findByActiveTrue();

    // Verificar si un cliente existe y está activo
    boolean existsByIdAndActiveTrue(Long id);
}
