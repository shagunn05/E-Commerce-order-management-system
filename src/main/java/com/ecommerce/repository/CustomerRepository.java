package com.ecommerce.repository;

import com.ecommerce.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    Optional<Customer> findByEmail(String email);
    List<Customer> findAll();
    boolean updateLoyaltyPoints(Long id, int points, String tier);
}