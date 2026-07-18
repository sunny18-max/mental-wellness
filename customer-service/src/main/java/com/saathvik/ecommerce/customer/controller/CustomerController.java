package com.saathvik.ecommerce.customer.controller;

import com.saathvik.ecommerce.customer.dto.CustomerResponse;
import com.saathvik.ecommerce.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/me")
    public CustomerResponse me(@AuthenticationPrincipal UserDetails userDetails) {
        return customerService.getByEmail(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable UUID id) {
        return customerService.getById(id);
    }

    @GetMapping
    public Page<CustomerResponse> getAll(Pageable pageable) {
        return customerService.getAll(pageable);
    }
}
