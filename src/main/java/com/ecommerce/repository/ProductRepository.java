package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Product entity hierarchy.
 * Demonstrates:
 * - Polymorphic querying across JOINED inheritance
 * - Atomic stock deduction via @Modifying @Query
 * - Derived query methods
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByStockQuantityLessThan(int threshold);

    /**
     * Atomic database-level stock deduction ensuring thread-safe inventory decrement.
     */
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity " +
           "WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int atomicDeductStock(@Param("id") Long id, @Param("quantity") int quantity);
}
