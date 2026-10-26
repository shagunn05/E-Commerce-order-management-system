package com.ecommerce.model;

import com.ecommerce.exception.EcommerceException;
import java.time.LocalDateTime;
import java.util.Objects;

public class Customer {

    private Long id;
    private String name;
    private String email;
    private CustomerTier tier;
    private int loyaltyPoints;
    private LocalDateTime createdAt;

    public Customer() {
        this.tier = CustomerTier.REGULAR;
        this.loyaltyPoints = 0;
        this.createdAt = LocalDateTime.now();
    }

    public Customer(Long id, String name, String email, CustomerTier tier, int loyaltyPoints) {
        this.id = id;
        setName(name);
        setEmail(email);
        this.tier = (tier != null) ? tier : CustomerTier.REGULAR;
        this.loyaltyPoints = Math.max(0, loyaltyPoints);
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new EcommerceException("Customer name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new EcommerceException("Invalid customer email address: " + email);
        }
        this.email = email.trim().toLowerCase();
    }

    public CustomerTier getTier() { return tier; }
    public void setTier(CustomerTier tier) { this.tier = (tier != null) ? tier : CustomerTier.REGULAR; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = Math.max(0, loyaltyPoints); }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

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
}