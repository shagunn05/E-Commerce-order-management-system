package com.managementsystem.util;

import com.managementsystem.exception.DuplicateItemException;
import com.managementsystem.exception.ManagementException;
import com.managementsystem.model.*;
import com.managementsystem.service.InventoryService;
import com.managementsystem.service.LibraryService;
import com.managementsystem.service.UserService;
import java.time.LocalDate;

public class DataInitializer {
    public static void initializeSampleData(InventoryService inventoryService,
                                            UserService userService,
                                            LibraryService libraryService) {
        try {
            // 1. Initialize Users (Members & Staff)
            Member alice = new Member("M-001", "Alice Johnson", "alice@univ.edu", 3);
            Member bob = new Member("M-002", "Bob Smith", "bob@univ.edu", 4);
            Member charlie = new Member("M-003", "Charlie Davis", "charlie@univ.edu", 2);
            Member diana = new Member("M-004", "Diana Prince", "diana@univ.edu", 5);
            Member evan = new Member("M-005", "Evan Wright", "evan@univ.edu", 3);

            userService.registerUser(alice);
            userService.registerUser(bob);
            userService.registerUser(charlie);
            userService.registerUser(diana);
            userService.registerUser(evan);

            StaffUser evelyn = new StaffUser("S-101", "Dr. Evelyn Reed", "evelyn@system.org", "Library Ops", "LIB-DIR");
            StaffUser marcus = new StaffUser("S-102", "Marcus Vance", "marcus@system.org", "Inventory", "INV-MGR");
            userService.registerUser(evelyn);
            userService.registerUser(marcus);

            // 2. Initialize Books
            Book b1 = new Book("BK-101", "Clean Code", "Robert C. Martin", "978-0132350884", "Software", 45.00, 3, 2, 14, 1.50);
            Book b2 = new Book("BK-102", "Effective Java", "Joshua Bloch", "978-0134685991", "Software", 50.00, 4, 1, 14, 2.00);
            Book b3 = new Book("BK-103", "Design Patterns (GoF)", "Erich Gamma et al.", "978-0201633610", "Software", 55.00, 2, 1, 14, 2.00);
            Book b4 = new Book("BK-104", "Introduction to Algorithms", "Cormen et al.", "978-0262033848", "CS/Algorithms", 85.00, 1, 2, 7, 3.00); // Low stock!
            Book b5 = new Book("BK-105", "Java Concurrency in Practice", "Brian Goetz", "978-0321349606", "Software", 48.00, 3, 1, 14, 2.00);

            inventoryService.addItem(b1);
            inventoryService.addItem(b2);
            inventoryService.addItem(b3);
            inventoryService.addItem(b4);
            inventoryService.addItem(b5);

            // 3. Initialize Electronics Assets
            ElectronicsItem e1 = new ElectronicsItem("EL-201", "Dell Latitude 5520 Laptop", "Dell", "5520", 950.00, 6, 2, 24, "SN-DELL");
            ElectronicsItem e2 = new ElectronicsItem("EL-202", "Honeywell Barcode Scanner", "Honeywell", "1202g", 180.00, 2, 3, 12, "SN-HON"); // Low stock!
            ElectronicsItem e3 = new ElectronicsItem("EL-203", "Epson EcoTank Wireless Printer", "Epson", "L3250", 240.00, 3, 1, 12, "SN-EPS");

            inventoryService.addItem(e1);
            inventoryService.addItem(e2);
            inventoryService.addItem(e3);

            // 4. Initialize Stationery Items
            StationeryItem s1 = new StationeryItem("ST-301", "A4 Copy Paper 80GSM Reams", "Carton of 5", "Paper", 35.00, 12, 5);
            StationeryItem s2 = new StationeryItem("ST-302", "Ballpoint Gel Pens Blue (50pk)", "Pack of 50", "Plastic", 14.50, 3, 5); // Low stock!
            StationeryItem s3 = new StationeryItem("ST-303", "Sticky Notes Neon 3x3 (12pk)", "Pack of 12", "Paper", 12.00, 18, 5);

            inventoryService.addItem(s1);
            inventoryService.addItem(s2);
            inventoryService.addItem(s3);

            // 5. Create a pre-existing overdue borrow record for Alice to demonstrate fines and overdue alerts
            BorrowRecord overdueRecord = new BorrowRecord(
                    "REC-PRE01", alice.getUserId(), b1.getId(), b1.getTitle(),
                    LocalDate.now().minusDays(20), b1.getMaxLoanDays()
            );
            b1.borrowCopy(alice.getUserId());
            alice.addBorrowRecord(overdueRecord);

        } catch (DuplicateItemException e) {
            System.err.println("Sample data initialization error: " + e.getMessage());
        } catch (ManagementException e) {
            System.err.println("Setup error: " + e.getMessage());
        }
    }
}
