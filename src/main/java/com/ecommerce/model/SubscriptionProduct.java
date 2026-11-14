package com.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Concrete Subscription Product Entity.
 * Inherits from Product table via JOINED inheritance.
 */
@Entity
@Table(name = "subscription_products")
@DiscriminatorValue("SUBSCRIPTION")
public class SubscriptionProduct extends Product {

    @Column(name = "billing_period_months", nullable = false)
    private int billingPeriodMonths = 1;

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

    public int getBillingPeriodMonths() {
        return billingPeriodMonths;
    }

    public void setBillingPeriodMonths(int billingPeriodMonths) {
        this.billingPeriodMonths = Math.max(1, billingPeriodMonths);
    }
}
