/**
 * File 1 of 6: Product.java
 *
 * Demonstrates:
 * 1. Abstraction (abstract class and interface contract)
 * 2. Encapsulation (private fields, validated getters/setters)
 * 3. Custom Exception Handling (InsufficientStockException, InvalidProductException)
 * 4. Collections Comparable interface for natural sorting
 */

// Custom Checked Exception for Stock Errors
class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}

// Custom Unchecked Exception for Invalid Product Data
class InvalidProductException extends RuntimeException {
    public InvalidProductException(String message) {
        super(message);
    }
}

// Interface for Abstraction & Polymorphism
interface Discountable {
    double calculateDiscount(int quantity);
    String getDiscountPolicy();
}

// Abstract Base Class
public abstract class Product implements Discountable, Comparable<Product> {

    // Encapsulation: private attributes
    private String productId;
    private String name;
    private double price;
    private int stockQuantity;

    public Product(String productId, String name, double price, int stockQuantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new InvalidProductException("Product ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductException("Product name cannot be null or empty.");
        }
        if (price < 0.0) {
            throw new InvalidProductException("Price cannot be negative: " + price);
        }
        if (stockQuantity < 0) {
            throw new InvalidProductException("Stock quantity cannot be negative: " + stockQuantity);
        }

        this.productId = productId.trim().toUpperCase();
        this.name = name.trim();
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Abstract methods to be implemented by specific subclasses (Polymorphism)
    public abstract double calculateTax();
    public abstract String getCategory();
    public abstract void displayDetails();

    // Defensive stock reduction with custom exception
    public void deductStock(int quantity) throws InsufficientStockException {
        if (quantity <= 0) {
            throw new InvalidProductException("Quantity to purchase must be greater than zero.");
        }
        if (quantity > this.stockQuantity) {
            throw new InsufficientStockException(
                String.format("Out of stock! Requested: %d, Available: %d for [%s] %s",
                    quantity, this.stockQuantity, this.productId, this.name)
            );
        }
        this.stockQuantity -= quantity;
    }

    // Restock method
    public void restock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidProductException("Restock quantity must be positive.");
        }
        this.stockQuantity += quantity;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0.0) {
            throw new InvalidProductException("Price cannot be negative: " + price);
        }
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    // Comparable implementation: sorts products by price ascending
    @Override
    public int compareTo(Product other) {
        return Double.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-25s | Price: $%-8.2f | Stock: %-4d",
                productId, name, price, stockQuantity);
    }
}
