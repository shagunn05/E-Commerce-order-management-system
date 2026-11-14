package com.managementsystem;

import com.managementsystem.concurrent.*;
import com.managementsystem.exception.ManagementException;
import com.managementsystem.model.*;
import com.managementsystem.repository.InMemoryRepository;
import com.managementsystem.repository.Repository;
import com.managementsystem.service.*;
import com.managementsystem.util.ConsoleFormatter;
import com.managementsystem.util.DataInitializer;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Main Entry Point for the Integrated Library and Inventory Management System.
 * Fully showcases:
 * 1. OOP (Encapsulation, Inheritance, Polymorphism, Abstraction)
 * 2. Java Collections Framework (List, Map, Set, PriorityQueue, ConcurrentHashMap, Streams)
 * 3. Robust Exception Handling (custom checked/unchecked hierarchy, try-catch-finally)
 * 4. Multithreading (BlockingQueue, ExecutorService, CountDownLatch, Worker Threads, Locks)
 */
public class Main {
    private final Repository<Item, String> itemRepository;
    private final Repository<User, String> userRepository;
    private final Repository<Transaction, String> transactionRepository;

    private final AsyncAuditLogger auditLogger;
    private final Thread auditLoggerThread;
    private final StockMonitorWorker stockMonitor;
    private final Thread stockMonitorThread;
    private final OverdueNotificationWorker overdueWorker;
    private final Thread overdueWorkerThread;

    private final InventoryService inventoryService;
    private final LibraryService libraryService;
    private final UserService userService;
    private final ConcurrentSimulationTest simulationTest;

    public Main() {
        // 1. Initialize Thread-Safe Repositories (Collections Framework)
        this.itemRepository = new InMemoryRepository<>(Item::getId);
        this.userRepository = new InMemoryRepository<>(User::getUserId);
        this.transactionRepository = new InMemoryRepository<>(Transaction::getTransactionId);

        // 2. Multithreading: Start Asynchronous Background Worker Threads
        this.auditLogger = new AsyncAuditLogger("logs/audit_trail.log");
        this.auditLoggerThread = new Thread(auditLogger, "AuditLoggerThread");
        this.auditLoggerThread.setDaemon(true);
        this.auditLoggerThread.start();

        this.stockMonitor = new StockMonitorWorker(itemRepository, 15000); // scans every 15s
        this.stockMonitorThread = new Thread(stockMonitor, "StockMonitorThread");
        this.stockMonitorThread.setDaemon(true);
        this.stockMonitorThread.start();

        this.overdueWorker = new OverdueNotificationWorker(userRepository, 20000); // scans every 20s
        this.overdueWorkerThread = new Thread(overdueWorker, "OverdueWorkerThread");
        this.overdueWorkerThread.setDaemon(true);
        this.overdueWorkerThread.start();

        // 3. Initialize Domain Services
        this.inventoryService = new InventoryService(itemRepository, transactionRepository, auditLogger);
        this.libraryService = new LibraryService(itemRepository, userRepository, transactionRepository, auditLogger);
        this.userService = new UserService(userRepository);
        this.simulationTest = new ConcurrentSimulationTest(libraryService);

        // 4. Prepopulate realistic sample data
        DataInitializer.initializeSampleData(inventoryService, userService, libraryService);

        // 5. Register JVM shutdown hook for clean resource disposal
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownWorkers, "ShutdownHook"));
    }

    private void shutdownWorkers() {
        System.out.println("\n[SYSTEM] Gracefully shutting down worker threads...");
        auditLogger.stop();
        stockMonitor.stop();
        overdueWorker.stop();
        try {
            auditLoggerThread.join(1000);
            stockMonitorThread.join(1000);
            overdueWorkerThread.join(1000);
        } catch (InterruptedException ignored) {}
        System.out.println("[SYSTEM] All threads terminated safely.");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        ConsoleFormatter.printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Enter choice (0-5): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleLibraryMenu(scanner);
                case "2" -> handleInventoryMenu(scanner);
                case "3" -> handleUserMenu(scanner);
                case "4" -> handleMultithreadingMenu(scanner);
                case "5" -> handleTransactionAuditMenu();
                case "0" -> {
                    ConsoleFormatter.printSuccess("Exiting system. Thank you!");
                    shutdownWorkers();
                    running = false;
                }
                default -> ConsoleFormatter.printWarning("Invalid choice! Please select 0 to 5.");
            }
        }
    }

    private void printMainMenu() {
        ConsoleFormatter.printSection("MAIN MENU");
        System.out.println("  1. Library Management (Books, Borrowing, Returns, Fines, Reservations)");
        System.out.println("  2. Inventory Operations (Stock, Dispense, Restock, Low-Stock, Valuation)");
        System.out.println("  3. Member & Staff Directory (Patrons, Staff, Registration)");
        System.out.println("  4. Multithreading Showcase (Live Concurrent Race Test & Background Workers)");
        System.out.println("  5. View Transaction Audit Log");
        System.out.println("  0. Exit System");
        System.out.println("------------------------------------------------------------------------");
    }

    // =========================================================================
    // 1. Library Submenu
    // =========================================================================
    private void handleLibraryMenu(Scanner scanner) {
        ConsoleFormatter.printSection("LIBRARY MANAGEMENT");
        System.out.println("  1. View All Books");
        System.out.println("  2. Borrow a Book");
        System.out.println("  3. Return a Book (Calculate Fines)");
        System.out.println("  4. Place Book Reservation (PriorityQueue Demo)");
        System.out.println("  5. View Member Active Loans");
        System.out.println("  6. Pay Member Overdue Fines");
        System.out.println("  0. Back to Main Menu");
        System.out.print("Select library action: ");
        String opt = scanner.nextLine().trim();

        try {
            switch (opt) {
                case "1" -> {
                    List<Book> books = libraryService.getAllBooks();
                    System.out.println("\n--- Current Library Catalog ---");
                    for (Book b : books) {
                        System.out.println(b);
                        System.out.println("    " + b.getDetailsSummary());
                    }
                }
                case "2" -> {
                    System.out.print("Enter Member ID (e.g. M-002, M-003): ");
                    String memberId = scanner.nextLine().trim();
                    System.out.print("Enter Book ID to borrow (e.g. BK-101, BK-102): ");
                    String bookId = scanner.nextLine().trim();

                    BorrowRecord record = libraryService.borrowBook(memberId, bookId);
                    ConsoleFormatter.printSuccess("Book successfully checked out! " + record);
                }
                case "3" -> {
                    System.out.print("Enter Member ID (e.g. M-001): ");
                    String memberId = scanner.nextLine().trim();
                    System.out.print("Enter Book ID to return (e.g. BK-101): ");
                    String bookId = scanner.nextLine().trim();

                    double fine = libraryService.returnBook(memberId, bookId, LocalDate.now());
                    if (fine > 0) {
                        ConsoleFormatter.printWarning(String.format("Book returned late! Overdue fine of $%.2f was added to member's account.", fine));
                    } else {
                        ConsoleFormatter.printSuccess("Book returned on time! No fine incurred.");
                    }
                }
                case "4" -> {
                    System.out.print("Enter Member ID: ");
                    String memberId = scanner.nextLine().trim();
                    System.out.print("Enter Book ID: ");
                    String bookId = scanner.nextLine().trim();
                    System.out.print("Select Priority Tier (1 = Faculty/VIP, 2 = Student, 3 = Guest): ");
                    int tier = Integer.parseInt(scanner.nextLine().trim());

                    libraryService.placeReservation(memberId, bookId, tier);
                    ConsoleFormatter.printSuccess("Reservation successfully added to the PriorityQueue for Book " + bookId);
                }
                case "5" -> {
                    System.out.print("Enter Member ID (e.g. M-001, M-002): ");
                    String memberId = scanner.nextLine().trim();
                    List<BorrowRecord> active = libraryService.getMemberActiveLoans(memberId);
                    System.out.println("\n--- Active Loans for Member " + memberId + " ---");
                    if (active.isEmpty()) {
                        System.out.println("  No active loans.");
                    } else {
                        for (BorrowRecord r : active) {
                            System.out.println("  " + r);
                        }
                    }
                }
                case "6" -> {
                    System.out.print("Enter Member ID: ");
                    String memberId = scanner.nextLine().trim();
                    User u = userService.getUser(memberId);
                    if (u instanceof Member m) {
                        System.out.printf("Current unpaid fines for %s: $%.2f\n", m.getName(), m.getUnpaidFines());
                        if (m.getUnpaidFines() > 0) {
                            System.out.print("Enter payment amount ($): ");
                            double amt = Double.parseDouble(scanner.nextLine().trim());
                            double change = libraryService.payFine(memberId, amt);
                            ConsoleFormatter.printSuccess(String.format("Payment processed! Remaining fine: $%.2f (Change returned: $%.2f)",
                                    m.getUnpaidFines(), change));
                        }
                    }
                }
                case "0" -> {}
                default -> ConsoleFormatter.printWarning("Invalid option!");
            }
        } catch (ManagementException e) {
            ConsoleFormatter.printError(e.toString());
        } catch (NumberFormatException e) {
            ConsoleFormatter.printError("Invalid numeric input!");
        }
    }

    // =========================================================================
    // 2. Inventory Submenu
    // =========================================================================
    private void handleInventoryMenu(Scanner scanner) {
        ConsoleFormatter.printSection("INVENTORY OPERATIONS");
        System.out.println("  1. View All Catalog / Inventory Items");
        System.out.println("  2. Search Items (Title, Author, Category, Brand)");
        System.out.println("  3. Issue / Dispense Stock (Thread-Safe Deduction)");
        System.out.println("  4. Restock Item (Replenishment)");
        System.out.println("  5. View Low Stock Items (Reorder Required)");
        System.out.println("  6. Inventory Financial Valuation (Streams Demo)");
        System.out.println("  0. Back to Main Menu");
        System.out.print("Select inventory action: ");
        String opt = scanner.nextLine().trim();

        try {
            switch (opt) {
                case "1" -> {
                    List<Item> items = inventoryService.getAllItems();
                    System.out.println("\n--- Complete Catalog & Inventory Items ---");
                    for (Item i : items) {
                        System.out.println(i);
                        System.out.println("    Details: " + i.getDetailsSummary());
                    }
                }
                case "2" -> {
                    System.out.print("Enter search keyword: ");
                    String query = scanner.nextLine().trim();
                    List<Item> results = inventoryService.search(query);
                    System.out.printf("\n--- Search Results for '%s' (%d items found) ---\n", query, results.size());
                    for (Item i : results) {
                        System.out.println("  " + i);
                    }
                }
                case "3" -> {
                    System.out.print("Enter Item ID to issue (e.g. ST-301, EL-201): ");
                    String itemId = scanner.nextLine().trim();
                    System.out.print("Enter quantity to dispense: ");
                    int qty = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Requester / User ID: ");
                    String uid = scanner.nextLine().trim();
                    System.out.print("Enter reason / purpose: ");
                    String reason = scanner.nextLine().trim();

                    inventoryService.issueStock(itemId, qty, uid, reason);
                    ConsoleFormatter.printSuccess(String.format("Successfully dispensed %d unit(s) of %s.", qty, itemId));
                }
                case "4" -> {
                    System.out.print("Enter Item ID to restock: ");
                    String itemId = scanner.nextLine().trim();
                    System.out.print("Enter quantity to add: ");
                    int qty = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Staff ID: ");
                    String staffId = scanner.nextLine().trim();

                    inventoryService.restock(itemId, qty, staffId);
                    ConsoleFormatter.printSuccess(String.format("Successfully restocked %d unit(s) of %s.", qty, itemId));
                }
                case "5" -> {
                    List<Item> low = inventoryService.getLowStockItems();
                    System.out.println("\n--- Low Stock Items Requiring Reorder ---");
                    if (low.isEmpty()) {
                        System.out.println("  All stock levels healthy!");
                    } else {
                        for (Item i : low) {
                            System.out.printf("  [REORDER] ID: %-8s | %-25s | Stock: %-3d | Threshold: %-3d\n",
                                    i.getId(), i.getTitle(), i.getStockQuantity(), i.getReorderThreshold());
                        }
                    }
                }
                case "6" -> {
                    double totalValue = inventoryService.calculateTotalValuation();
                    ConsoleFormatter.printSection("INVENTORY FINANCIAL REPORT");
                    System.out.printf("  Total Catalog Valuation: $%,.2f\n", totalValue);
                    System.out.printf("  Total Unique SKUs/Items: %d\n", inventoryService.getAllItems().size());
                }
                case "0" -> {}
                default -> ConsoleFormatter.printWarning("Invalid option!");
            }
        } catch (ManagementException e) {
            ConsoleFormatter.printError(e.toString());
        } catch (NumberFormatException e) {
            ConsoleFormatter.printError("Invalid numeric input!");
        }
    }

    // =========================================================================
    // 3. User & Member Submenu
    // =========================================================================
    private void handleUserMenu(Scanner scanner) {
        ConsoleFormatter.printSection("USER & MEMBER DIRECTORY");
        System.out.println("  1. List All Users (Patrons & Staff)");
        System.out.println("  2. Register New Member");
        System.out.println("  3. Register New Staff");
        System.out.println("  0. Back to Main Menu");
        System.out.print("Select user action: ");
        String opt = scanner.nextLine().trim();

        try {
            switch (opt) {
                case "1" -> {
                    List<User> users = userService.getAllUsers();
                    System.out.println("\n--- Registered Users ---");
                    for (User u : users) {
                        System.out.println(u + " | " + u.getUserSummary());
                    }
                }
                case "2" -> {
                    System.out.print("Enter User ID (e.g. M-010): ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter Full Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter Max Borrow Limit (e.g. 3, 5): ");
                    int limit = Integer.parseInt(scanner.nextLine().trim());

                    Member member = new Member(id, name, email, limit);
                    userService.registerUser(member);
                    ConsoleFormatter.printSuccess("New member registered: " + member);
                }
                case "3" -> {
                    System.out.print("Enter Staff ID (e.g. S-201): ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter Full Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter Department: ");
                    String dept = scanner.nextLine().trim();
                    System.out.print("Enter Staff Code: ");
                    String code = scanner.nextLine().trim();

                    StaffUser staff = new StaffUser(id, name, email, dept, code);
                    userService.registerUser(staff);
                    ConsoleFormatter.printSuccess("New staff registered: " + staff);
                }
                case "0" -> {}
                default -> ConsoleFormatter.printWarning("Invalid option!");
            }
        } catch (ManagementException e) {
            ConsoleFormatter.printError(e.toString());
        } catch (NumberFormatException e) {
            ConsoleFormatter.printError("Invalid numeric input!");
        }
    }

    // =========================================================================
    // 4. Multithreading Showcase
    // =========================================================================
    private void handleMultithreadingMenu(Scanner scanner) {
        ConsoleFormatter.printSection("MULTITHREADING SHOWCASE & CONCURRENCY LAB");
        System.out.println("  1. Run Live Concurrent Checkout Stress Test (Race Condition Prevention)");
        System.out.println("  2. View Background Worker Thread Status & Audit Logger Metrics");
        System.out.println("  0. Back to Main Menu");
        System.out.print("Select concurrency test option: ");
        String opt = scanner.nextLine().trim();

        switch (opt) {
            case "1" -> {
                // Setup book with only 2 copies available
                Book targetBook = new Book(
                        "BK-RACE",
                        "High-Performance Java Concurrency",
                        "Dr. Heinz Kabutz",
                        "978-0000000001",
                        "Computing",
                        60.00,
                        2, // ONLY 2 COPIES!
                        1, 14, 2.0
                );
                try {
                    itemRepository.save(targetBook);
                } catch (Exception ignored) {}

                // Create 6 simulated concurrent patron members
                Member[] testMembers = new Member[6];
                for (int i = 0; i < 6; i++) {
                    String uid = "SIM-M0" + (i + 1);
                    Member m = new Member(uid, "Simulated Patron #" + (i + 1), uid + "@sim.com", 3);
                    userRepository.save(m);
                    testMembers[i] = m;
                }

                // Run simulation
                simulationTest.runSimulation(targetBook, testMembers);
            }
            case "2" -> {
                ConsoleFormatter.printSection("THREAD STATUS & METRICS");
                System.out.printf("  - AuditLogger Thread:      %s (Daemon: %b)\n",
                        auditLoggerThread.getState(), auditLoggerThread.isDaemon());
                System.out.printf("  - Logged Events in Queue:  %d waiting\n", auditLogger.getQueueSize());
                System.out.printf("  - Total Written to Disk:   %d transactions\n", auditLogger.getLoggedCount());
                System.out.printf("  - StockMonitor Thread:     %s (Periodic reorder checks)\n", stockMonitorThread.getState());
                System.out.printf("  - OverdueWorker Thread:    %s (Periodic fine scans)\n", overdueWorkerThread.getState());
                System.out.printf("  - Total Active JVM Threads:%d\n", Thread.activeCount());
            }
            case "0" -> {}
            default -> ConsoleFormatter.printWarning("Invalid option!");
        }
    }

    // =========================================================================
    // 5. Transaction Audit Menu
    // =========================================================================
    private void handleTransactionAuditMenu() {
        ConsoleFormatter.printSection("TRANSACTION AUDIT LOG");
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) {
            System.out.println("  No transactions recorded yet.");
        } else {
            for (Transaction tx : transactions) {
                System.out.println("  " + tx);
            }
        }
    }

    public static void main(String[] args) {
        new Main().start();
    }
}
