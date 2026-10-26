package com.ecommerce.model;

public interface Discountable {
    double calculateDiscount(int quantity, CustomerTier tier);
}
