package co.unimagdalena.tiendauni.repository;

import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.enums.CustomerStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryIntegrationTest extends AbstractRepositoryIT {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void guardarYBuscarClientePorEmail() {
        Customer customer = Customer.builder()
                .firstName("Andres")
                .lastName("Polo")
                .email("andres.polo@test.com")
                .status(CustomerStatus.ACTIVE)
                .build();

        Customer guardado = customerRepository.save(customer);
        Optional<Customer> encontrado = customerRepository.findByEmail("andres.polo@test.com");

        assertThat(guardado.getId()).isNotNull();
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isEqualTo(guardado.getId());
        assertThat(customerRepository.existsByEmail("andres.polo@test.com")).isTrue();
    }

    @Test
    void buscarClientesPorEstadoYExistenciaPorEstado() {
        Customer activo = customerRepository.save(Customer.builder()
                .firstName("Laura")
                .lastName("Nuñez")
                .email("laura.nunez@test.com")
                .status(CustomerStatus.ACTIVE)
                .build());

        Customer inactivo = customerRepository.save(Customer.builder()
                .firstName("Pedro")
                .lastName("Gomez")
                .email("pedro.gomez@test.com")
                .status(CustomerStatus.INACTIVE)
                .build());

        List<Customer> activos = customerRepository.findByStatus(CustomerStatus.ACTIVE);

        assertThat(activos).extracting(Customer::getId).containsExactly(activo.getId());
        assertThat(customerRepository.existsByIdAndStatus(activo.getId(), CustomerStatus.ACTIVE)).isTrue();
        assertThat(customerRepository.existsByIdAndStatus(inactivo.getId(), CustomerStatus.ACTIVE)).isFalse();
    }
}
