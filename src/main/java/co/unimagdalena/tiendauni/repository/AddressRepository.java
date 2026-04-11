package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByCustomerId(Long customerId);

    // Validar que una dirección pertenece a un cliente específico
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Address a " +
           "WHERE a.id = :addressId AND a.customer.id = :customerId")
    boolean existsByIdAndCustomerId(@Param("addressId") Long addressId, @Param("customerId") Long customerId);

    // Buscar una dirección por ID y cliente
    Optional<Address> findByIdAndCustomerId(Long id, Long customerId);
}
