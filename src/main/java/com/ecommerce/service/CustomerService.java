package com.ecommerce.service;

import com.ecommerce.dto.CustomerRegistrationDto;
import com.ecommerce.exception.DuplicateResourceException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Customer;
import com.ecommerce.model.CustomerTier;
import com.ecommerce.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service managing Customer account registration, tier status, and lookup.
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer registerCustomer(CustomerRegistrationDto dto) {
        if (customerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Customer", "email", dto.getEmail());
        }

        Customer customer = new Customer(null, dto.getName(), dto.getEmail(), CustomerTier.REGULAR, 0);
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with email", email));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
