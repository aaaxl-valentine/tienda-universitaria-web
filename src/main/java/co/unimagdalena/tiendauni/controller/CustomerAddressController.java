package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.AddressDTOs.AddressResponse;
import co.unimagdalena.tiendauni.DTOs.AddressDTOs.CreateAddressRequest;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.repository.CustomerRepository;
import co.unimagdalena.tiendauni.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final AddressService addressService;
    private final CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @PathVariable long customerId,
            @Valid @RequestBody CreateAddressRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con ID " + customerId + " no encontrado"));
        var normalizedRequest = new CreateAddressRequest(
                request.street(),
                request.city(),
                request.department(),
                request.postalCode(),
                request.isDefault(),
                customerId
        );
        var addressCreated = addressService.CreateAddress(normalizedRequest, customer);
        var location = uriBuilder.path("/api/addresses/{id}").buildAndExpand(addressCreated.id()).toUri();
        return ResponseEntity.created(location).body(addressCreated);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddressesByCustomer(@PathVariable long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Cliente con ID " + customerId + " no encontrado");
        }
        return ResponseEntity.ok(addressService.getAddressesByCustomerId(customerId));
    }
}
