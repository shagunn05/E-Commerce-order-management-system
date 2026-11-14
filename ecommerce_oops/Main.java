import java.util.*;

/**
 * File 6 of 6: Main.java
 *
 * The Executable Main Entry Point for the E-Commerce Order Management System.
 * Fully showcases and executes:
 * 1. Object-Oriented Programming (Abstraction, Encapsulation, Inheritance, Polymorphism)
 * 2. Java Collections Framework (List, Map, Set, Collections.sort with Comparable)
 * 3. Robust Exception Handling (custom InsufficientStockException and InvalidProductException)
 * 4. Interactive Console Mode & Automated Verification Suite
 */
public class Main {

    private static final List<Product> catalog = new ArrayList<>();
    private static final Map<String, Product> productMap = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("        WELCOME TO THE JAVA OOP E-COMMERCE ORDER MANAGEMENT SYSTEM               ");
        System.out.println("================================================================================");

        // 1. Initialize Product Catalog (Polymorphism & Inheritance)
        initializeCatalog();

        // 2. Run Automated Verification Test Suite (Demonstrating all OOP & Exceptions)
        runAutomatedTestSuite();

        // 3. Interactive Menu for Live Demonstration
        runInteractiveMenu();
    }

    private static void initializeCatalog() {
        // Polymorphic initialization: Product reference pointing to diverse subclasses
        Product p1 = new ElectronicsProduct("E-101", "Pro Gaming Laptop", 1200.00, 5, "Asus", 24);
        Product p2 = new ElectronicsProduct("E-102", "Wireless Headphones", 150.00, 20, "Sony", 12);
        Product p3 = new ClothingProduct("C-201", "Denim Jacket", 80.00, 15, "L", "Denim");
        Product p4 = new ClothingProduct("C-202", "Organic Cotton T-Shirt", 25.00, 50, "M", "100% Cotton");
        Product p5 = new ElectronicsProduct("E-103", "Smart Watch Series 7", 300.00, 8, "Apple", 12);

        catalog.addAll(Arrays.asList(p1, p2, p3, p4, p5));

        for (Product p : catalog) {
            productMap.put(p.getProductId(), p);
        }
    }

    /**
     * Demonstrates OOP concepts, Collections, and Exception Handling automatically.
     */
    private static void runAutomatedTestSuite() {
        System.out.println("\n[PHASE 1] DEMONSTRATING OOP & COLLECTIONS (CATALOG SORTING)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Catalog sorted naturally by Price (Comparable<Product>):");
        Collections.sort(catalog); // Uses compareTo
        for (Product p : catalog) {
            System.out.println("  " + p);
        }

        System.out.println("\n[PHASE 2] DEMONSTRATING POLYMORPHISM & DETAILED ITEM SPECIFICATIONS");
        System.out.println("--------------------------------------------------------------------------------");
        for (Product p : catalog) {
            p.displayDetails(); // Dynamic method dispatch!
        }

        System.out.println("\n[PHASE 3] DEMONSTRATING EXCEPTION HANDLING (TRY-CATCH BLOCKS)");
        System.out.println("--------------------------------------------------------------------------------");

        // Exception Test 1: Negative Price Validation
        try {
            System.out.println("1. Attempting to create an illegal product with negative price (-$45.00)...");
            Product invalidProduct = new ElectronicsProduct("ERR-01", "Faulty Widget", -45.00, 10, "BadBrand", 12);
            System.out.println("   [FAILED] Product created unexpectedly: " + invalidProduct);
        } catch (InvalidProductException ex) {
            System.out.println("   [CAUGHT EXPECTED EXCEPTION] " + ex.getMessage());
        }

        // Exception Test 2: Insufficient Stock
        Customer testCustomer = new Customer("CUST-99", "Sarah Connor", "sarah@resistance.com");
        Order testOrder = new Order("ORD-ERR-TEST", testCustomer);

        try {
            Product laptop = productMap.get("E-101"); // Has only 5 in stock
            System.out.printf("2. Attempting to purchase 25 units of '%s' (Stock available: %d)...\n",
                    laptop.getName(), laptop.getStockQuantity());
            testOrder.addItem(laptop, 25); // Should trigger exception!
            System.out.println("   [FAILED] Order added item unexpectedly!");
        } catch (InsufficientStockException ex) {
            System.out.println("   [CAUGHT EXPECTED EXCEPTION] " + ex.getMessage());
        }

        System.out.println("\n[PHASE 4] DEMONSTRATING VALID ORDER PLACEMENT & INVOICE GENERATION");
        System.out.println("--------------------------------------------------------------------------------");

        Customer vipCustomer = new Customer("CUST-01", "Alex Mercer", "alex.mercer@apex.com");
        // Give Alex initial loyalty points to reach Gold tier
        vipCustomer.addLoyaltyPoints(3000); // 300 points -> Gold Tier (5% discount)

        Order validOrder = new Order("ORD-2026-9001", vipCustomer);
        try {
            // Add 2 Laptops (triggers 10% Electronics bulk discount)
            validOrder.addItem(productMap.get("E-101"), 2);
            // Add 3 T-Shirts (triggers 15% Clothing bundle discount)
            validOrder.addItem(productMap.get("C-202"), 3);
            // Add 1 Headphones
            validOrder.addItem(productMap.get("E-102"), 1);

            validOrder.confirmOrder();
            validOrder.printInvoice();

            System.out.printf("[RESULT] Customer updated profile: %s\n", vipCustomer);
        } catch (InsufficientStockException e) {
            System.err.println("Unexpected stock error: " + e.getMessage());
        }
    }

    /**
     * Interactive console menu allowing manual user operations.
     */
    private static void runInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        Customer activeCustomer = new Customer("CUST-100", "Current User", "user@ecommerce.com");
        Order activeOrder = new Order("ORD-LIVE-001", activeCustomer);

        boolean running = true;
        while (running) {
            System.out.println("\n=========================== INTERACTIVE MENU ===========================");
            System.out.println("  1. Browse Product Catalog");
            System.out.println("  2. Add Product to Current Order");
            System.out.println("  3. View Current Order & Print Invoice");
            System.out.println("  4. Trigger Exception Handling Demo");
            System.out.println("  5. View Customer Loyalty Profile");
            System.out.println("  0. Exit System");
            System.out.println("========================================================================");
            System.out.print("Enter your choice (0-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println("\n--- Current Available Catalog ---");
                    for (Product p : catalog) {
                        System.out.printf("[%s] %-24s | Category: %-12s | Price: $%-7.2f | Stock: %d\n",
                                p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), p.getStockQuantity());
                    }
                }
                case "2" -> {
                    System.out.print("Enter Product ID to add (e.g. E-101, E-102, C-201, C-202): ");
                    String pid = scanner.nextLine().trim().toUpperCase();
                    Product p = productMap.get(pid);

                    if (p == null) {
                        System.out.println("[ERROR] Product ID '" + pid + "' not found in catalog!");
                        break;
                    }

                    System.out.print("Enter quantity to purchase: ");
                    try {
                        int qty = Integer.parseInt(scanner.nextLine().trim());
                        activeOrder.addItem(p, qty);
                        System.out.printf("[SUCCESS] Added %d unit(s) of '%s' to order.\n", qty, p.getName());
                    } catch (NumberFormatException e) {
                        System.out.println("[ERROR] Invalid number format!");
                    } catch (InsufficientStockException e) {
                        System.out.println("[STOCK EXCEPTION CAUGHT] " + e.getMessage());
                    }
                }
                case "3" -> {
                    if (activeOrder.getItems().isEmpty()) {
                        System.out.println("\n[INFO] Your order is currently empty! Add items using option 2.");
                    } else {
                        activeOrder.printInvoice();
                    }
                }
                case "4" -> {
                    System.out.println("\n--- Exception Handling Interactive Sandbox ---");
                    System.out.println("Testing InsufficientStockException: Requesting 999 units of E-101...");
                    try {
                        activeOrder.addItem(productMap.get("E-101"), 999);
                    } catch (InsufficientStockException e) {
                        System.out.println("[SUCCESSFULLY INTERCEPTED] Exception message: " + e.getMessage());
                    }
                }
                case "5" -> {
                    System.out.println("\n--- Customer Profile & Loyalty Status ---");
                    System.out.println("  " + activeCustomer);
                }
                case "0" -> {
                    System.out.println("\nThank you for exploring the Java OOP E-Commerce System. Goodbye!");
                    running = false;
                }
                default -> System.out.println("[WARNING] Invalid choice! Please select 0 to 5.");
            }
        }
    }
}
