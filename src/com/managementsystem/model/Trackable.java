package com.managementsystem.model;

import com.managementsystem.exception.InsufficientStockException;

/**
 * Interface demonstrating Abstraction for items tracked in inventory.
 */
public interface Trackable {
    int getStockQuantity();
    void addStock(int count);
    void deductStock(int count) throws InsufficientStockException;
    boolean isLowStock();
    int getReorderThreshold();
}
