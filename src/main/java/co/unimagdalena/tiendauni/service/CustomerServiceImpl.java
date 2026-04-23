package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.*;
import co.unimagdalena.tiendauni.NotFoundException.ResourceNotFoundException;
import co.unimagdalena.tiendauni.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import co.unimagdalena.tiendauni.repository.CustomerRepository;
import co.unimagdalena.tiendauni.service.mappers.*;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;


    @Override
    public CustomerResponse Create(CreateCustomerRequest request) {
            Customer customer = CustomerMapper.toEntity(request);

            Customer GuardarCustomer = customerRepository.save(customer);

            return CustomerMapper.toResponse(GuardarCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(
                        () -> new    ResourceNotFoundException("Usuario %d no encontrado".formatted(id))
                );
        return CustomerMapper.toResponse(customer);
    }

    @Transactional
    @Override
    public CustomerResponse updateById(long id, UpdateCustomerRequest request) {
        var m = customerRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuario %d no encontrado".formatted(id))
                );
        CustomerMapper.patch(m, request);
        return CustomerMapper.toResponse(m);
    }

    @Override
    public Page<CustomerResponse> findAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(CustomerMapper::toResponse);
    }


    @Override
    public void deleteCustomerById(long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario %d no encontrado".formatted(id));
        }
        customerRepository.deleteById(id);
    }
}
