package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.AddressDTOs.AddressResponse;
import co.unimagdalena.tiendauni.DTOs.AddressDTOs.CreateAddressRequest;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.entity.Address;
import co.unimagdalena.tiendauni.entity.Customer;
import co.unimagdalena.tiendauni.repository.AddressRepository;
import co.unimagdalena.tiendauni.service.mappers.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public AddressResponse CreateAddress(CreateAddressRequest request, Customer customer) {
        Address address = AddressMapper.toEntity(request, customer);
        Address savedAddress = addressRepository.save(address);
        return AddressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse GetAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion %d no encontrada".formatted(id)));
        return AddressMapper.toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddresses(Pageable pageable) {
        return addressRepository.findAll(pageable)
                .map(AddressMapper::toResponse)
                .getContent();
    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    @Transactional
    public AddressResponse UpdateAddress(Long id, CreateAddressRequest request, Customer customer) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion %d no encontrada".formatted(id)));

        address.setStreet(request.street());
        address.setCity(request.city());
        address.setDepartment(request.department());
        address.setPostalCode(request.postalCode());
        address.setIsDefault(request.isDefault());
        address.setCustomer(customer);

        return AddressMapper.toResponse(address);
    }
}
