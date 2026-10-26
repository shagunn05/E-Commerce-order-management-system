package com.ecommerce.model;

import com.ecommerce.exception.EcommerceException;
import java.util.Objects;

public abstract class Product implements Discountable {

    private Long id;
    private String sku;
    private String name;
    private String category;
    private double price;
    private int stockQuantity;

    public Product() {
    }

    public Product(Long id, String sku, String name, String category, double price, int stockQuantity) {
        setId(id);
        setSku(sku);
        setName(name);
        setCategory(category);
        setPrice(price);
        setStockQuantity(stockQuantity);
    }

    public abstract String getProductType();
    public abstract double calculateShippingCost();

    @Override
    public double calculateDiscount(int quantity, CustomerTier tier) {
        double baseDiscount = this.price * quantity * tier.getDiscountRate();
        double bulkDiscount = 0.0;
        if (quantity >= 5) {
            bulkDiscount = (this.price * quantity) * 0.05;
        }
        return Math.round((baseDiscount + bulkDiscount) * 100.0) / 100.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }

    public void setSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new EcommerceException("Product SKU cannot be null or empty.");
        }
        this.sku = sku.trim().toUpperCase();
    }

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new EcommerceException("Product name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getCategory() { return category; }

    public void setCategory(String category) {
        this.category = (category == null || category.trim().isEmpty()) ? "General" : category.trim();
    }

    public double getPrice() { return price; }

    public void setPrice(double price) {
        if (price < 0.0) {
            throw new EcommerceException("Product price cannot be negative: " + price);
        }
        this.price = Math.round(price * 100.0) / 100.0;
    }

    public int getStockQuantity() { return stockQuantity; }

    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new EcommerceException("Stock quantity cannot be negative: " + stockQuantity);
        }
        this.stockQuantity = stockQuantity;
    }

    public synchronized void deductStock(int amount) {
        if (amount <= 0) {
            throw new EcommerceException("Deduction quantity must be positive.");
        }
        if (this.stockQuantity < amount) {
            throw new EcommerceException("Insufficient stock for SKU " + this.sku + ". Available: " + this.stockQuantity + ", Requested: " + amount);
        }
        this.stockQuantity -= amount;
    }

    public synchronized void restock(int amount) {
        if (amount <= 0) {
            throw new EcommerceException("Restock quantity must be positive.");
        }
        this.stockQuantity += amount;
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
}