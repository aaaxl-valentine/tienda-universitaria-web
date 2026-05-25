package co.unimagdalena.tiendauni.controlador;

import co.unimagdalena.tiendauni.DTOs.AddressDTOs.*;
import co.unimagdalena.tiendauni.controller.AddressController;
import co.unimagdalena.tiendauni.entity.Address;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.enums.CustomerStatus;
import co.unimagdalena.tiendauni.repository.CustomerRepository;
import co.unimagdalena.tiendauni.service.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AddressController addressController;

    @Test
    void createAddress_ShouldReturnCreated() {
        var customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .status(CustomerStatus.ACTIVE)
                .build();

        var request = new CreateAddressRequest(
                "Calle 1 # 2-3",
                "Santa Marta",
                "Magdalena",
                "470001",
                true,
                1L
        );

        var response = new AddressResponse(
                1L,
                "Calle 1 # 2-3",
                "Santa Marta",
                "Magdalena",
                "470001",
                true,
                1L
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressService.CreateAddress(any(CreateAddressRequest.class), eq(customer)))
                .thenReturn(response);

        var result = addressController.createAddress(request, org.springframework.web.util.UriComponentsBuilder.newInstance());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAddress_ShouldReturnOk() {
        var response = new AddressResponse(
                1L,
                "Calle 1 # 2-3",
                "Santa Marta",
                "Magdalena",
                "470001",
                true,
                1L
        );

        when(addressService.GetAddress(1L)).thenReturn(response);

        var result = addressController.getAddress(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAllAddresses_ShouldReturnList() {
        var response1 = new AddressResponse(1L, "Dir 1", "Santa Marta", "Magdalena", "470001", true, 1L);
        var response2 = new AddressResponse(2L, "Dir 2", "Bogota", "Cundinamarca", "110111", false, 1L);

        when(addressService.getAllAddresses(any(PageRequest.class)))
                .thenReturn(List.of(response1, response2));

        var result = addressController.getAllAddresses(0, 10);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody().get(0).id()).isEqualTo(1L);
        assertThat(result.getBody().get(1).id()).isEqualTo(2L);
    }

    @Test
    void getAddressesByCustomer_ShouldReturnList() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        var response1 = new AddressResponse(1L, "Dir 1", "Santa Marta", "Magdalena", "470001", true, 1L);
        when(addressService.getAddressesByCustomerId(1L))
                .thenReturn(List.of(response1));

        var result = addressController.getAddressesByCustomer(1L, 0, 10);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void updateAddress_ShouldReturnOk() {
        var customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        var request = new CreateAddressRequest(
                "Nueva calle",
                "Medellin",
                "Antioquia",
                "050001",
                false,
                1L
        );

        var response = new AddressResponse(
                1L,
                "Nueva calle",
                "Medellin",
                "Antioquia",
                "050001",
                false,
                1L
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(addressService.UpdateAddress(eq(1L), any(CreateAddressRequest.class), eq(customer)))
                .thenReturn(response);

        var result = addressController.updateAddress(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deleteAddress_ShouldReturnNoContent() {
        var result = addressController.deleteAddress(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(addressService).deleteAddress(1L);
    }
}
