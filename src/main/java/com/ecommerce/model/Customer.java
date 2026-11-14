package com.ecommerce.model;

import com.ecommerce.exception.EcommerceException;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer JPA Entity & Domain Aggregate.
 * Demonstrates:
 * - Hibernate ORM: @Entity, @Table, @Id, @GeneratedValue, @Version (Optimistic Locking)
 * - Domain Invariants: Validation on state mutation and automated loyalty tier progression
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CustomerTier tier = CustomerTier.REGULAR;

    @Column(name = "loyalty_points", nullable = false)
    private int loyaltyPoints = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version;

    public Customer() {
    }

    public Customer(Long id, String name, String email, CustomerTier tier, int loyaltyPoints) {
        this.id = id;
        setName(name);
        setEmail(email);
        this.tier = (tier != null) ? tier : CustomerTier.REGULAR;
        this.loyaltyPoints = Math.max(0, loyaltyPoints);
        this.createdAt = LocalDateTime.now();
    }

    public Customer(String name, String email) {
        this(null, name, email, CustomerTier.REGULAR, 0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new EcommerceException("Customer name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new EcommerceException("Invalid customer email address: " + email);
        }
        this.email = email.trim().toLowerCase();
    }

    public CustomerTier getTier() {
        return tier;
    }

    public void setTier(CustomerTier tier) {
        this.tier = (tier != null) ? tier : CustomerTier.REGULAR;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = Math.max(0, loyaltyPoints);
    }

    /**
     * Domain Business Rule:
     * Increment loyalty points and dynamically upgrade customer tier based on milestone thresholds.
     */
    public void addLoyaltyPoints(int points) {
        if (points > 0) {
            this.loyaltyPoints += points;
            if (this.loyaltyPoints >= 1000) {
                this.tier = CustomerTier.PLATINUM;
            } else if (this.loyaltyPoints >= 500) {
                this.tier = CustomerTier.GOLD;
            } else if (this.loyaltyPoints >= 200) {
                this.tier = CustomerTier.SILVER;
            }
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return String.format("Customer[ID=%d, Name='%s', Tier=%s, Points=%d]", id, name, tier, loyaltyPoints);
    }
}
