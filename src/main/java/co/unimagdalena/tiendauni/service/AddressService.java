package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.AddressDTOs.*;
import co.unimagdalena.tiendauni.entity.Customer;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddressService {
    AddressResponse CreateAddress(CreateAddressRequest request, Customer customer);
    AddressResponse GetAddress(Long id);
    List<AddressResponse> getAllAddresses(Pageable pageable);
    List<AddressResponse> getAddressesByCustomerId(Long customerId);
    void deleteAddress(Long id);
    AddressResponse UpdateAddress(Long id, CreateAddressRequest request, Customer customer);
}
