package com.example.brokage.service;

import com.example.brokage.model.Customer;
import com.example.brokage.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer registerCustomer(Customer registrationRequest) {
        if (customerRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists.");
        }
        Customer newCustomer = new Customer();
        newCustomer.setUsername(registrationRequest.getUsername());
        newCustomer.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        newCustomer.setRole("CUSTOMER");
        return customerRepository.save(newCustomer);
    }
}
