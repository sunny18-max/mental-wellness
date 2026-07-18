package com.saathvik.ecommerce.customer.service;

import com.saathvik.ecommerce.customer.dto.CustomerResponse;
import com.saathvik.ecommerce.customer.entity.Customer;
import com.saathvik.ecommerce.customer.exception.ApiExceptions;
import com.saathvik.ecommerce.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse getByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ApiExceptions.CustomerNotFoundException(email));
        return toResponse(customer);
    }

    public CustomerResponse getById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiExceptions.CustomerNotFoundException(id.toString()));
        return toResponse(customer);
    }

    public Page<CustomerResponse> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toResponse);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getFirstName(), customer.getLastName(),
                customer.getEmail(), customer.getRole().name());
    }
}
