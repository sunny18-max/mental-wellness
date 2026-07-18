package com.saathvik.ecommerce.customer.service;

import com.saathvik.ecommerce.customer.dto.AuthResponse;
import com.saathvik.ecommerce.customer.dto.LoginRequest;
import com.saathvik.ecommerce.customer.dto.RegisterRequest;
import com.saathvik.ecommerce.customer.entity.Customer;
import com.saathvik.ecommerce.customer.exception.ApiExceptions;
import com.saathvik.ecommerce.customer.repository.CustomerRepository;
import com.saathvik.ecommerce.customer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new ApiExceptions.EmailAlreadyExistsException(request.email());
        }

        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Customer.Role.CUSTOMER)
                .build();

        customerRepository.save(customer);

        String token = jwtService.generateToken(
                User.withUsername(customer.getEmail()).password(customer.getPassword()).authorities("ROLE_" + customer.getRole()).build(),
                Map.of("role", customer.getRole().name(), "customerId", customer.getId().toString())
        );

        return new AuthResponse(token, customer.getEmail(), customer.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (Exception ex) {
            throw new ApiExceptions.InvalidCredentialsException();
        }

        Customer customer = customerRepository.findByEmail(request.email())
                .orElseThrow(ApiExceptions.InvalidCredentialsException::new);

        String token = jwtService.generateToken(
                User.withUsername(customer.getEmail()).password(customer.getPassword()).authorities("ROLE_" + customer.getRole()).build(),
                Map.of("role", customer.getRole().name(), "customerId", customer.getId().toString())
        );

        return new AuthResponse(token, customer.getEmail(), customer.getRole().name());
    }
}
