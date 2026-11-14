package com.ecommerce.model;

/**
 * Interface demonstrating Abstraction and Polymorphism.
 * Any product or service offering custom discount calculations implements this contract.
 */
public interface Discountable {
    
    /**
     * Calculate discount amount based on purchase quantity and customer tier.
     *
     * @param quantity Number of units purchased
     * @param tier     The loyalty tier of the customer
     * @return Total discount amount deducted from the gross price
     */
    double calculateDiscount(int quantity, CustomerTier tier);
}
