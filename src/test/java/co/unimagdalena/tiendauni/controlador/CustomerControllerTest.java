package co.unimagdalena.tiendauni.controlador;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.*;
import co.unimagdalena.tiendauni.controller.CustomerController;
import co.unimagdalena.tiendauni.entity.enums.CustomerStatus;
import co.unimagdalena.tiendauni.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void createCustomer_ShouldReturnCreated() {
        var request = new CreateCustomerRequest("John", "Doe", "john.doe@example.com", CustomerStatus.ACTIVE);
        var response = new CustomerResponse(1L, "John", "Doe", "john.doe@example.com", CustomerStatus.ACTIVE, LocalDateTime.now());

        when(customerService.Create(any(CreateCustomerRequest.class))).thenReturn(response);

        var result = customerController.createCustomer(request, UriComponentsBuilder.newInstance());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getCustomer_ShouldReturnOk() {
        var response = new CustomerResponse(1L, "John", "Doe", "john.doe@example.com", CustomerStatus.ACTIVE, LocalDateTime.now());

        when(customerService.getCustomerById(1L)).thenReturn(response);

        var result = customerController.getCustomer(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAllCustomers_ShouldReturnList() {
        var response = new CustomerResponse(1L, "John", "Doe", "john.doe@example.com", CustomerStatus.ACTIVE, LocalDateTime.now());
        var page = new PageImpl<>(List.of(response));

        when(customerService.findAllCustomers(any())).thenReturn(page);

        var result = customerController.getAllCustomers(0, 5);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().getFirst()).isEqualTo(response);
    }

    @Test
    void updateCustomer_ShouldReturnOk() {
        var request = new UpdateCustomerRequest("Jane", null, null, null);
        var response = new CustomerResponse(1L, "Jane", "Doe", "john.doe@example.com", CustomerStatus.ACTIVE, LocalDateTime.now());

        when(customerService.updateById(eq(1L), any(UpdateCustomerRequest.class))).thenReturn(response);

        var result = customerController.updateCustomer(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deleteCustomer_ShouldReturnNoContent() {
        var result = customerController.deleteCustomer(1L);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
