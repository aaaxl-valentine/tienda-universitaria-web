package co.unimagdalena.tiendauni.controller;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.*;
import co.unimagdalena.tiendauni.service.CustomerService;
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
@RequestMapping("/api/constumers")
@RequiredArgsConstructor
@Validated
public class CustomerController {
    private final CustomerService costumerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            UriComponentsBuilder uriBuilder
    ){
        var customerCreated = costumerService.Create(request);
        var location = uriBuilder.path("/api/constumers/{id}").buildAndExpand(customerCreated.id()).toUri();
        return ResponseEntity.created(location).body(customerCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable long id){
        return ResponseEntity.ok(costumerService.getCustomerById(id));
    }
    @GetMapping()
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        var resultado = costumerService.findAllCustomers(PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(resultado.getContent());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request
    ){
        return ResponseEntity.ok(costumerService.updateById(id, request));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomerResponse> deleteCustomer(@PathVariable long id){
        costumerService.deleteCustomerById(id);
        return ResponseEntity.noContent().build();
    }
}
