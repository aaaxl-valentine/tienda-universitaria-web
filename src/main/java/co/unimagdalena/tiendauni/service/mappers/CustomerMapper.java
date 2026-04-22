package co.unimagdalena.tiendauni.service.mappers;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.*;
import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.CreateCustomerRequest;
import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.CustomerResponse;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.entity.enums.CustomerStatus;

import java.lang.reflect.Member;

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

    //para actualizar el customer
    public static void patch(Customer customer, UpdateCustomerRequest request) {
        if(request.firstName() != null) customer.setFirstName(request.firstName());
        if(request.lastName() != null) customer.setLastName(request.lastName());
        if(request.email() != null) customer.setEmail(request.email());
        if(request.status() != null) customer.setStatus(request.status());
    }
}
