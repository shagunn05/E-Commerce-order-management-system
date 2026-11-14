/**
 * File 2 of 6: ElectronicsProduct.java
 *
 * Demonstrates:
 * 1. Inheritance (extends Product)
 * 2. Polymorphism (overrides calculateTax, calculateDiscount, displayDetails)
 * 3. Encapsulation (brand, warrantyMonths)
 */
public class ElectronicsProduct extends Product {

    private String brand;
    private int warrantyMonths;

    public ElectronicsProduct(String productId, String name, double price, int stockQuantity, String brand, int warrantyMonths) {
        super(productId, name, price, stockQuantity);
        this.brand = (brand != null && !brand.trim().isEmpty()) ? brand.trim() : "Generic";
        this.warrantyMonths = Math.max(0, warrantyMonths);
    }

    // Polymorphism: 18% standard electronic gadget sales tax
    @Override
    public double calculateTax() {
        return getPrice() * 0.18;
    }

    // Polymorphism: 10% volume discount if buying 2 or more electronic units
    @Override
    public double calculateDiscount(int quantity) {
        if (quantity >= 2) {
            return (getPrice() * quantity) * 0.10;
        }
        return 0.0;
    }

    @Override
    public String getDiscountPolicy() {
        return "10% Bulk Discount on 2+ units";
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }

    @Override
    public void displayDetails() {
        System.out.printf("  [Electronics] %s by %s | Warranty: %d months | Price: $%.2f | Tax: 18%%\n",
                getName(), brand, warrantyMonths, getPrice());
    }

    public String getBrand() {
        return brand;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }
}
