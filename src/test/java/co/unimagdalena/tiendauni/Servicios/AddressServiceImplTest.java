package co.unimagdalena.tiendauni.Servicios;

import co.unimagdalena.tiendauni.DTOs.AddressDTOs.AddressResponse;
import co.unimagdalena.tiendauni.DTOs.AddressDTOs.CreateAddressRequest;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.entity.Address;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.repository.AddressRepository;
import co.unimagdalena.tiendauni.service.AddressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl service;

    @Test
    void shouldCreateAddress() {
        Customer customer = customer(10L);
        CreateAddressRequest request = new CreateAddressRequest(
                "Calle 1 # 2-3",
                "Santa Marta",
                "Magdalena",
                "470001",
                true,
                10L
        );

        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(99L);
            return address;
        });

        AddressResponse response = service.CreateAddress(request, customer);

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.street()).isEqualTo("Calle 1 # 2-3");
        assertThat(response.city()).isEqualTo("Santa Marta");
        assertThat(response.customerId()).isEqualTo(10L);
        assertThat(response.isDefault()).isTrue();
    }

    @Test
    void shouldGetAddressById() {
        Address address = address(11L, "Cra 5 # 10-20", "Barranquilla", "Atlantico", "080001", false, customer(7L));
        when(addressRepository.findById(11L)).thenReturn(Optional.of(address));

        AddressResponse response = service.GetAddress(11L);

        assertThat(response.id()).isEqualTo(11L);
        assertThat(response.street()).isEqualTo("Cra 5 # 10-20");
        assertThat(response.city()).isEqualTo("Barranquilla");
        assertThat(response.customerId()).isEqualTo(7L);
        assertThat(response.isDefault()).isFalse();
    }

    @Test
    void shouldThrowWhenAddressNotFound() {
        when(addressRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.GetAddress(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldGetAllAddresses() {
        Pageable pageable = PageRequest.of(0, 2);
        Address a1 = address(1L, "Dir 1", "Santa Marta", "Magdalena", "470001", true, customer(1L));
        Address a2 = address(2L, "Dir 2", "Bogota", "Cundinamarca", "110111", false, customer(2L));

        when(addressRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(a1, a2), pageable, 2));

        List<AddressResponse> response = service.getAllAddresses(pageable);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).id()).isEqualTo(1L);
        assertThat(response.get(1).id()).isEqualTo(2L);
    }

    @Test
    void shouldUpdateAddress() {
        Customer originalCustomer = customer(1L);
        Customer newCustomer = customer(2L);
        Address existing = address(50L, "Antigua", "Santa Marta", "Magdalena", "470001", false, originalCustomer);

        CreateAddressRequest request = new CreateAddressRequest(
                "Nueva",
                "Medellin",
                "Antioquia",
                "050001",
                true,
                2L
        );

        when(addressRepository.findById(50L)).thenReturn(Optional.of(existing));

        AddressResponse response = service.UpdateAddress(50L, request, newCustomer);

        assertThat(response.id()).isEqualTo(50L);
        assertThat(response.street()).isEqualTo("Nueva");
        assertThat(response.city()).isEqualTo("Medellin");
        assertThat(response.department()).isEqualTo("Antioquia");
        assertThat(response.postalCode()).isEqualTo("050001");
        assertThat(response.isDefault()).isTrue();
        assertThat(response.customerId()).isEqualTo(2L);
    }

    @Test
    void shouldDeleteAddress() {
        when(addressRepository.existsById(77L)).thenReturn(true);
        service.deleteAddress(77L);
        verify(addressRepository).deleteById(77L);
    }

    private Customer customer(Long id) {
        return Customer.builder()
                .id(id)
                .firstName("Nombre")
                .lastName("Apellido")
                .email("mail" + id + "@example.com")
                .build();
    }

    private Address address(
            Long id,
            String street,
            String city,
            String department,
            String postalCode,
            boolean isDefault,
            Customer customer
    ) {
        return Address.builder()
                .id(id)
                .street(street)
                .city(city)
                .department(department)
                .postalCode(postalCode)
                .isDefault(isDefault)
                .customer(customer)
                .build();
    }
}
