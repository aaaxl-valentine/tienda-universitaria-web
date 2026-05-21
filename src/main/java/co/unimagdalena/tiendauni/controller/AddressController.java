package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.AddressDTOs.*;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.repository.CustomerRepository;
import co.unimagdalena.tiendauni.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Validated
public class AddressController {

    private final AddressService addressService;
    private final CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody CreateAddressRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con ID " + request.customerId() + " no encontrado"));
        var addressCreated = addressService.CreateAddress(request, customer);
        var location = uriBuilder.path("/api/addresses/{id}").buildAndExpand(addressCreated.id()).toUri();
        return ResponseEntity.created(location).body(addressCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable long id) {
        return ResponseEntity.ok(addressService.GetAddress(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AddressResponse>> getAddressesByCustomer(
            @PathVariable long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Cliente con ID " + customerId + " no encontrado");
        }
        return ResponseEntity.ok(addressService.getAddressesByCustomerId(customerId));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return ResponseEntity.ok(addressService.getAllAddresses(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable long id,
            @Valid @RequestBody CreateAddressRequest request
    ) {
        var customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con ID " + request.customerId() + " no encontrado"));
        return ResponseEntity.ok(addressService.UpdateAddress(id, request, customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
