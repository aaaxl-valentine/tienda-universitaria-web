package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.enums.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    // Buscar clientes por estado
    List<Customer> findByStatus(CustomerStatus status);

    // Verificar si un cliente existe y coincide con el estado
    boolean existsByIdAndStatus(Long id, CustomerStatus status);
}