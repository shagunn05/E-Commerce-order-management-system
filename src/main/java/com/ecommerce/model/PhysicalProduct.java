package com.ecommerce.model;

public class PhysicalProduct extends Product {

    private double weightKg;

    public PhysicalProduct() {
        super();
    }

    public PhysicalProduct(Long id, String sku, String name, String category, double price, int stockQuantity, double weightKg) {
        super(id, sku, name, category, price, stockQuantity);
        setWeightKg(weightKg);
    }

    @Override
    public String getProductType() {
        return "PHYSICAL";
    }

    @Override
    public double calculateShippingCost() {
        return Math.round((4.50 + (this.weightKg * 2.00)) * 100.0) / 100.0;
    }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = Math.max(0.0, weightKg); }
}