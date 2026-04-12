package co.unimagdalena.tiendauni.Servicios;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.*;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.enums.CustomerStatus;
import co.unimagdalena.tiendauni.repository.CustomerRepository;
import co.unimagdalena.tiendauni.service.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {
    @Mock
    CustomerRepository customerRepository;
    @InjectMocks
    CustomerServiceImpl customerServiceImpl;


    @Test
    void createCustomerReturnResponseDTO() {
        var request = new CreateCustomerRequest(
                "Manuel",
                "Sierra",
                "Sierra@gmail.com",
                CustomerStatus.ACTIVE
        );
        when(customerRepository.save(any())).thenAnswer(
                invocacion -> {
                    Customer customer = (Customer) invocacion.getArguments()[0];
                    customer.setId(11L);
                    return customer;
                }
        );

        var response = customerServiceImpl.Create(request);

        assertThat(request.firstName()).isEqualTo("Manuel");
        assertThat(request.lastName()).isEqualTo("Sierra");
        assertThat(request.email()).isEqualTo("Sierra@gmail.com");
        assertThat(request.status()).isEqualTo(CustomerStatus.ACTIVE);
    }
    @Test
    void ShouldUpdateViaPatch(){
        Customer customer = Customer.builder()
                .id(11L)
                .firstName("Manuel")
                .lastName("Sierra")
                .email("manue@gmail.com")
                .status(CustomerStatus.ACTIVE)
                .build();

        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Andres",
                null,
                "Andres@gmail.com",
                CustomerStatus.INACTIVE
        );
        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        var response = customerServiceImpl.updateById(11L, request);

        assertThat(request.firstName()).isEqualTo("Andres");
        assertThat(request.lastName()).isEqualTo(null);
        assertThat(request.email()).isEqualTo("Andres@gmail.com");
        assertThat(request.status()).isEqualTo(CustomerStatus.INACTIVE);


    }
    @Test
    void GetAllCustomers() {
        Pageable pageable = PageRequest.of(0,2);

        Customer c1 = Customer.builder()
                .id(1L)
                .firstName("Manuel")
                .lastName("Sierra")
                .email("Manuel@gmail.com")
                .status(CustomerStatus.ACTIVE)
                .build();

        Customer c2 = Customer.builder()
                .id(2L)
                .firstName("Carlos")
                .lastName("Perez")
                .email("Carlos@gmail.com")
                .status(CustomerStatus.INACTIVE)
                .build();


        Page<Customer> page = new PageImpl<>(List.of(c1, c2), pageable, 2);

        when(customerRepository.findAll(pageable)).thenReturn(page);

        Page<CustomerResponse> response = customerServiceImpl.findAllCustomers(pageable);

        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).firstName()).isEqualTo("Manuel");
        assertThat(response.getContent().get(0).lastName()).isEqualTo("Sierra");
        assertThat(response.getContent().get(1).firstName()).isEqualTo("Carlos");
        assertThat(response.getContent().get(1).lastName()).isEqualTo("Perez");
    }
    @Test
    void GetCustomerById() {
        long id = 1L;

        Customer customer = Customer.builder()
                .id(id)
                .firstName("Manuel")
                .lastName("Sierra")
                .email("Manuel@gmail.com")
                .status(CustomerStatus.ACTIVE)
                .build();

        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        CustomerResponse response = customerServiceImpl.getCustomerById(id);

        assertThat(response.firstName()).isEqualTo("Manuel");
        assertThat(response.lastName()).isEqualTo("Sierra");
        assertThat(response.email()).isEqualTo("Manuel@gmail.com");
        assertThat(response.status()).isEqualTo(CustomerStatus.ACTIVE);
    }
}
