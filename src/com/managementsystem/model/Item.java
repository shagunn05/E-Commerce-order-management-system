package com.managementsystem.model;

import com.managementsystem.exception.InsufficientStockException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract Base Class demonstrating:
 * 1. Abstraction (abstract methods, hides stock locking details)
 * 2. Encapsulation (private fields, protected mutators, validation)
 * 3. Thread-safety (internal ReentrantLock per item for concurrent safety)
 */
public abstract class Item implements Searchable, Trackable, Comparable<Item> {
    private final String id;
    private String title;
    private String category;
    private double unitPrice;
    private int quantity;
    private int reorderThreshold;
    private final ReentrantLock itemLock;

    public Item(String id, String title, String category, double unitPrice, int initialQuantity, int reorderThreshold) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be empty");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Initial quantity cannot be negative");
        }
        this.id = id.trim();
        this.title = title != null ? title.trim() : "Untitled";
        this.category = category != null ? category.trim() : "General";
        this.unitPrice = unitPrice;
        this.quantity = initialQuantity;
        this.reorderThreshold = Math.max(reorderThreshold, 0);
        this.itemLock = new ReentrantLock();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            this.category = category.trim();
        }
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice >= 0) {
            this.unitPrice = unitPrice;
        }
    }

    @Override
    public int getStockQuantity() {
        itemLock.lock();
        try {
            return quantity;
        } finally {
            itemLock.unlock();
        }
    }

    @Override
    public void addStock(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Stock to add must be positive");
        }
        itemLock.lock();
        try {
            this.quantity += count;
        } finally {
            itemLock.unlock();
        }
    }

    @Override
    public void deductStock(int count) throws InsufficientStockException {
        if (count <= 0) {
            throw new IllegalArgumentException("Stock count to deduct must be positive");
        }
        itemLock.lock();
        try {
            if (this.quantity < count) {
                throw new InsufficientStockException(this.id, count, this.quantity);
            }
            this.quantity -= count;
        } finally {
            itemLock.unlock();
        }
    }

    @Override
    public boolean isLowStock() {
        itemLock.lock();
        try {
            return this.quantity <= this.reorderThreshold;
        } finally {
            itemLock.unlock();
        }
    }

    @Override
    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = Math.max(reorderThreshold, 0);
    }

    public ReentrantLock getItemLock() {
        return itemLock;
    }

    // Abstract methods demonstrating Polymorphism in subclasses
    public abstract String getItemType();
    public abstract String getDetailsSummary();

    @Override
    public boolean matches(String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        String q = query.toLowerCase();
        return id.toLowerCase().contains(q) ||
               title.toLowerCase().contains(q) ||
               category.toLowerCase().contains(q);
    }

    @Override
    public int compareTo(Item other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %-8s | Title: %-25s | Cat: %-12s | Price: $%-6.2f | Stock: %-4d",
                getItemType(), id, title, category, unitPrice, getStockQuantity());
    }
}
