package co.unimagdalena.tiendauni.DTOs;

import java.io.Serializable;

public class AddressDTOs {
    public record CreateAddressRequest(
            String street,
            String city,
            String department,
            String postalCode,
            boolean isDefault,
            Long customerId
    ) implements Serializable {}

    public record AddressResponse(
            Long id,
            String street,
            String city,
            String department,
            String postalCode,
            boolean isDefault,
            Long customerId
    ) implements Serializable {}
}