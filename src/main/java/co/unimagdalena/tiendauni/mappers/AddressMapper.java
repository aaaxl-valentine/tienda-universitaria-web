package co.unimagdalena.tiendauni.service.mapper;

import co.unimagdalena.tiendauni.DTOs.AddressDTOs.CreateAddressRequest;
import co.unimagdalena.tiendauni.DTOs.AddressDTOs.AddressResponse;
import co.unimagdalena.tiendauni.entity.Address;
import co.unimagdalena.tiendauni.entity.Customer;

public class AddressMapper {

    public static Address toEntity(CreateAddressRequest request, Customer customer) {
        return Address.builder()
                .street(request.street())
                .city(request.city())
                .department(request.department())
                .postalCode(request.postalCode())
                .isDefault(request.isDefault())
                .customer(customer)
                .build();
    }

    public static AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getDepartment(),
                address.getPostalCode(),
                address.getIsDefault(),
                address.getCustomer() != null ? address.getCustomer().getId() : null
        );
    }
}
