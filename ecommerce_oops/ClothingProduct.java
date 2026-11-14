/**
 * File 3 of 6: ClothingProduct.java
 *
 * Demonstrates:
 * 1. Inheritance (extends Product)
 * 2. Polymorphism (overrides calculateTax, calculateDiscount, displayDetails)
 * 3. Encapsulation (size, material)
 */
public class ClothingProduct extends Product {

    private String size;
    private String material;

    public ClothingProduct(String productId, String name, double price, int stockQuantity, String size, String material) {
        super(productId, name, price, stockQuantity);
        this.size = (size != null && !size.trim().isEmpty()) ? size.trim().toUpperCase() : "M";
        this.material = (material != null && !material.trim().isEmpty()) ? material.trim() : "Cotton";
    }

    // Polymorphism: 5% textile sales tax
    @Override
    public double calculateTax() {
        return getPrice() * 0.05;
    }

    // Polymorphism: 15% seasonal bundle discount if buying 3 or more clothing items
    @Override
    public double calculateDiscount(int quantity) {
        if (quantity >= 3) {
            return (getPrice() * quantity) * 0.15;
        }
        return 0.0;
    }

    @Override
    public String getDiscountPolicy() {
        return "15% Bundle Discount on 3+ units";
    }

    @Override
    public String getCategory() {
        return "Clothing";
    }

    @Override
    public void displayDetails() {
        System.out.printf("  [Clothing] %s | Size: %s | Material: %s | Price: $%.2f | Tax: 5%%\n",
                getName(), size, material, getPrice());
    }

    public String getSize() {
        return size;
    }

    public String getMaterial() {
        return material;
    }
}
