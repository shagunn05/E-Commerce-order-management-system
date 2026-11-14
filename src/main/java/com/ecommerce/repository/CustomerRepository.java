package com.ecommerce.repository;

import com.ecommerce.model.Customer;
import com.ecommerce.model.CustomerTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Customer entity.
 * Demonstrates:
 * - JpaRepository query derivation methods
 * - JPQL query methods with @Query
 * - PostgreSQL native execution managed by Hibernate ORM
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Customer> findByTier(CustomerTier tier);

    @Query("SELECT c FROM Customer c WHERE c.loyaltyPoints >= :minPoints ORDER BY c.loyaltyPoints DESC")
    List<Customer> findTopLoyalCustomers(@Param("minPoints") int minPoints);
}
