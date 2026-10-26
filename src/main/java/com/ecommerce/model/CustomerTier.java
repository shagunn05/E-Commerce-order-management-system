package com.ecommerce.model;

public enum CustomerTier {
    REGULAR(1, 0.00),
    SILVER(2, 0.05),
    GOLD(3, 0.10),
    PLATINUM(4, 0.15);

    private final int priority;
    private final double discountRate;

    CustomerTier(int priority, double discountRate) {
        this.priority = priority;
        this.discountRate = discountRate;
    }

    public int getPriority() {
        return priority;
    }

    public double getDiscountRate() {
        return discountRate;
    }
}