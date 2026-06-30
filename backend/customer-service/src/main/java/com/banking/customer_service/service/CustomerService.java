package com.banking.customer_service.service;

import com.banking.customer_service.dto.CustomerRequest;
import com.banking.customer_service.entity.Customer;
import com.banking.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomerProfile(CustomerRequest request) {
        // 1. Check if this user already has a profile
        Optional<Customer> existingCustomer = customerRepository.findByUsername(request.getUsername());
        if (existingCustomer.isPresent()) {
            throw new RuntimeException("Error: A profile already exists for username: " + request.getUsername());
        }

        // 2. Map DTO to Entity
        Customer newCustomer = Customer.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        // 3. Save to database
        return customerRepository.save(newCustomer);
    }

    // Add this helper method!
    public Optional<Customer> getCustomerByUsername(String username) {
        return customerRepository.findByUsername(username);
    }
}
