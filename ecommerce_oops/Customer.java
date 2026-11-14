/**
 * File 4 of 6: Customer.java
 *
 * Demonstrates:
 * 1. Encapsulation (private fields with domain invariants)
 * 2. Business Logic (loyalty point progression and membership tier discounts)
 */
public class Customer {

    private String customerId;
    private String name;
    private String email;
    private String membershipTier; // Regular, Silver, Gold, Platinum
    private int loyaltyPoints;

    public Customer(String customerId, String name, String email) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        this.customerId = customerId.trim();
        this.name = name.trim();
        this.email = email.trim();
        this.membershipTier = "Regular";
        this.loyaltyPoints = 0;
    }

    // Awards loyalty points (1 point per $10 spent) and upgrades tier automatically
    public void addLoyaltyPoints(double amountSpent) {
        int earned = (int) (amountSpent / 10.0);
        this.loyaltyPoints += earned;

        // Dynamic tier upgrades based on loyalty points
        if (this.loyaltyPoints >= 500) {
            this.membershipTier = "Platinum";
        } else if (this.loyaltyPoints >= 250) {
            this.membershipTier = "Gold";
        } else if (this.loyaltyPoints >= 100) {
            this.membershipTier = "Silver";
        }
    }

    // Additional customer loyalty discount based on tier
    public double getTierDiscountRate() {
        return switch (this.membershipTier) {
            case "Platinum" -> 0.10; // 10% off entire order
            case "Gold"     -> 0.05; // 5% off entire order
            case "Silver"   -> 0.02; // 2% off entire order
            default         -> 0.00;
        };
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | Tier: %s | Loyalty Points: %d",
                name, email, membershipTier, loyaltyPoints);
    }
}
