package co.unimagdalena.tiendauni.service.mapper;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.CreateCustomerRequest;
import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.CustomerResponse;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.enums.CustomerStatus;

public class CustomerMapper {

    public static Customer toEntity(CreateCustomerRequest request) {
        return Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .status(request.status() != null ? request.status() : CustomerStatus.ACTIVE)
                .build();
    }

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getStatus(),
                customer.getCreatedAt()
        );
    }
}
