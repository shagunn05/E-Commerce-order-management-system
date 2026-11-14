package com.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Concrete Physical Product Entity.
 * Inherits from Product table via JOINED inheritance.
 */
@Entity
@Table(name = "physical_products")
@DiscriminatorValue("PHYSICAL")
public class PhysicalProduct extends Product {

    @Column(name = "weight_kg", nullable = false)
    private double weightKg;

    public PhysicalProduct() {
        super();
    }

    public PhysicalProduct(Long id, String sku, String name, String category, double price, int stockQuantity, double weightKg) {
        super(id, sku, name, category, price, stockQuantity);
        this.weightKg = Math.max(0.1, weightKg);
    }

    @Override
    public String getProductType() {
        return "PHYSICAL";
    }

    @Override
    public double calculateShippingCost() {
        // Base shipping $5.00 plus $2.50 per kg
        return Math.round((5.00 + (this.weightKg * 2.50)) * 100.0) / 100.0;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = Math.max(0.1, weightKg);
    }
}
