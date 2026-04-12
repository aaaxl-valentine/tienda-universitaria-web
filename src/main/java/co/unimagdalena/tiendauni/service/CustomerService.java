package co.unimagdalena.tiendauni.service;

import co.unimagdalena.tiendauni.DTOs.CustomerDTOs.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CustomerService {
    CustomerResponse Create(CreateCustomerRequest request);
    CustomerResponse getCustomerById(long id);
    CustomerResponse updateById(long id, UpdateCustomerRequest request);
    Page<CustomerResponse> findAllCustomers(Pageable pageable);
    void deleteCustomerById(long id);
}
