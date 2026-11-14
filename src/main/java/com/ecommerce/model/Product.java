package com.ecommerce.model;

import com.ecommerce.exception.EcommerceException;
import com.ecommerce.exception.InsufficientStockException;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract Base JPA Entity for Products using JOINED inheritance strategy.
 * Demonstrates:
 * - Hibernate ORM: @Inheritance(strategy = InheritanceType.JOINED), @DiscriminatorColumn
 * - Concurrency Control: @Version (Optimistic Locking) to prevent race conditions during inventory deductions
 * - OOP: Abstraction, Encapsulation, Polymorphism (Discountable, shipping calculations)
 */
@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Product implements Discountable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private double price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Product() {
    }

    public Product(Long id, String sku, String name, String category, double price, int stockQuantity) {
        this.id = id;
        setSku(sku);
        setName(name);
        setCategory(category);
        setPrice(price);
        setStockQuantity(stockQuantity);
        this.createdAt = LocalDateTime.now();
    }

    // Abstract methods overridden by subclasses (Polymorphism)
    public abstract String getProductType();
    public abstract double calculateShippingCost();

    // Template method for calculating discounts
    @Override
    public double calculateDiscount(int quantity, CustomerTier tier) {
        double baseDiscount = this.price * quantity * tier.getDiscountRate();
        double bulkDiscount = 0.0;
        if (quantity >= 5) {
            bulkDiscount = (this.price * quantity) * 0.05; // Extra 5% bulk discount for 5+ items
        }
        return Math.round((baseDiscount + bulkDiscount) * 100.0) / 100.0;
    }

    /**
     * Domain Business Rule: Stock Reservation & Deduction with invariant enforcement.
     */
    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new EcommerceException("Quantity to deduct must be strictly positive: " + quantity);
        }
        if (this.stockQuantity < quantity) {
            throw new InsufficientStockException(this.name, quantity, this.stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Domain Business Rule: Restocking inventory.
     */
    public void replenishStock(int quantity) {
        if (quantity <= 0) {
            throw new EcommerceException("Quantity to replenish must be strictly positive: " + quantity);
        }
        this.stockQuantity += quantity;
    }

    public boolean hasSufficientStock(int quantity) {
        return this.stockQuantity >= quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new EcommerceException("Product SKU cannot be null or empty.");
        }
        this.sku = sku.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new EcommerceException("Product name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new EcommerceException("Product category cannot be null or empty.");
        }
        this.category = category.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0.0) {
            throw new EcommerceException("Product price cannot be negative.");
        }
        this.price = Math.round(price * 100.0) / 100.0;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new EcommerceException("Product stock cannot be negative.");
        }
        this.stockQuantity = stockQuantity;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return String.format("%s [SKU=%s, Name='%s', Price=$%.2f, Stock=%d]",
                getProductType(), sku, name, price, stockQuantity);
    }
}
