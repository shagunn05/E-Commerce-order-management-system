import java.util.*;

/**
 * File 5 of 6: Order.java
 *
 * Demonstrates:
 * 1. Encapsulation & Aggregation
 * 2. Java Collections Framework:
 *    - List<Product> (Ordered sequence of items)
 *    - Map<Product, Integer> (Key-value mapping of product to ordered quantity)
 *    - Set<String> (Unique collection of product categories in order)
 * 3. Exception Handling (Throws InsufficientStockException)
 * 4. Polymorphism (Invoking calculateDiscount and calculateTax dynamically on subclasses)
 */
public class Order {

    private String orderId;
    private Customer customer;
    private String orderDate;
    private String status; // PENDING, CONFIRMED, CANCELLED

    // Collections Framework
    private List<Product> items;
    private Map<Product, Integer> itemQuantities;
    private Set<String> categories;

    public Order(String orderId, Customer customer) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be empty.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Order must be associated with a valid customer.");
        }

        this.orderId = orderId.trim();
        this.customer = customer;
        this.orderDate = java.time.LocalDate.now().toString();
        this.status = "PENDING";

        // Initialize collections
        this.items = new ArrayList<>();
        this.itemQuantities = new LinkedHashMap<>();
        this.categories = new HashSet<>();
    }

    /**
     * Adds a product to the order.
     * Demonstrates Exception Handling: throws InsufficientStockException if requested > stock.
     */
    public void addItem(Product product, int quantity) throws InsufficientStockException {
        if (product == null) {
            throw new IllegalArgumentException("Cannot add null product to order.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        // Defensive check: will throw InsufficientStockException if quantity > available
        product.deductStock(quantity);

        // Update Collections
        if (itemQuantities.containsKey(product)) {
            itemQuantities.put(product, itemQuantities.get(product) + quantity);
        } else {
            items.add(product);
            itemQuantities.put(product, quantity);
        }
        categories.add(product.getCategory());
    }

    /**
     * Removes an item from the order and restores the stock.
     */
    public void removeItem(Product product) {
        if (itemQuantities.containsKey(product)) {
            int quantity = itemQuantities.get(product);
            product.restock(quantity);
            itemQuantities.remove(product);
            items.remove(product);

            // Rebuild categories set
            categories.clear();
            for (Product p : items) {
                categories.add(p.getCategory());
            }
        }
    }

    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (Map.Entry<Product, Integer> entry : itemQuantities.entrySet()) {
            subtotal += entry.getKey().getPrice() * entry.getValue();
        }
        return subtotal;
    }

    /**
     * Dynamic Polymorphic Discount Calculation:
     * Combines Product-specific discounts (Electronics bulk, Clothing bundle)
     * with the Customer's Loyalty Tier discount.
     */
    public double calculateTotalDiscount() {
        double productDiscounts = 0.0;
        for (Map.Entry<Product, Integer> entry : itemQuantities.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            productDiscounts += p.calculateDiscount(qty); // Polymorphism!
        }

        double subtotal = calculateSubtotal();
        double tierDiscount = (subtotal - productDiscounts) * customer.getTierDiscountRate();

        return productDiscounts + tierDiscount;
    }

    /**
     * Dynamic Polymorphic Tax Calculation:
     * Electronics taxed at 18%, Clothing taxed at 5%.
     */
    public double calculateTotalTax() {
        double totalTax = 0.0;
        for (Map.Entry<Product, Integer> entry : itemQuantities.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            totalTax += p.calculateTax() * qty; // Polymorphism!
        }
        return totalTax;
    }

    public double calculateGrandTotal() {
        double subtotal = calculateSubtotal();
        double discount = calculateTotalDiscount();
        double tax = calculateTotalTax();
        return Math.max(0.0, (subtotal - discount) + tax);
    }

    /**
     * Confirms the order and credits loyalty points to customer.
     */
    public void confirmOrder() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm an empty order.");
        }
        this.status = "CONFIRMED";
        this.customer.addLoyaltyPoints(calculateGrandTotal());
    }

    /**
     * Prints an itemized ASCII invoice demonstrating all OOP calculations.
     */
    public void printInvoice() {
        System.out.println("\n================================================================================");
        System.out.printf("                         E-COMMERCE ORDER INVOICE\n");
        System.out.println("================================================================================");
        System.out.printf("Order ID   : %-20s  Date  : %s\n", orderId, orderDate);
        System.out.printf("Customer   : %-20s  Tier  : %s\n", customer.getName(), customer.getMembershipTier());
        System.out.printf("Status     : %-20s  Categories: %s\n", status, categories);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-10s %-25s %-12s %-6s %-10s %-10s\n",
                "ID", "Product Name", "Category", "Qty", "Unit Price", "Subtotal");
        System.out.println("--------------------------------------------------------------------------------");

        for (Map.Entry<Product, Integer> entry : itemQuantities.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            double lineTotal = p.getPrice() * qty;
            System.out.printf("%-10s %-25s %-12s %-6d $%-9.2f $%-9.2f\n",
                    p.getProductId(), p.getName(), p.getCategory(), qty, p.getPrice(), lineTotal);
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Gross Subtotal               : $%.2f\n", calculateSubtotal());
        System.out.printf("Discounts (Product + Tier)   : -$%.2f\n", calculateTotalDiscount());
        System.out.printf("Applicable Sales Tax         : +$%.2f\n", calculateTotalTax());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("GRAND TOTAL PAYABLE          : $%.2f\n", calculateGrandTotal());
        System.out.println("================================================================================\n");
    }

    public String getOrderId() { return orderId; }
    public Customer getCustomer() { return customer; }
    public String getStatus() { return status; }
    public List<Product> getItems() { return Collections.unmodifiableList(items); }
    public Set<String> getCategories() { return Collections.unmodifiableSet(categories); }
}
