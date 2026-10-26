
package com.ecommerce.model;

public class SubscriptionProduct extends Product {

    private int billingPeriodMonths;

    public SubscriptionProduct() {
        super();
    }

    public SubscriptionProduct(Long id, String sku, String name, String category, double price, int stockQuantity, int billingPeriodMonths) {
        super(id, sku, name, category, price, stockQuantity);
        this.billingPeriodMonths = Math.max(1, billingPeriodMonths);
    }

    @Override
    public String getProductType() {
        return "SUBSCRIPTION";
    }

    @Override
    public double calculateShippingCost() {
        return 0.0;
    }

    @Override
    public double calculateDiscount(int quantity, CustomerTier tier) {
        double discount = super.calculateDiscount(quantity, tier);
        if (this.billingPeriodMonths >= 12) {
            discount += (getPrice() * quantity) * 0.10;
        }
        return Math.round(discount * 100.0) / 100.0;
    }

    public int getBillingPeriodMonths() { return billingPeriodMonths; }
    public void setBillingPeriodMonths(int billingPeriodMonths) { this.billingPeriodMonths = Math.max(1, billingPeriodMonths); }
}