package com.managementsystem.util;

public class ConsoleFormatter {
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BOLD = "\u001B[1m";

    public static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("  ============================================================================");
        System.out.println("    INTEGRATED ENTERPRISE LIBRARY & INVENTORY MANAGEMENT SYSTEM (Java 27)");
        System.out.println("    Architecture: OOP | Collections Framework | Exception Handling | Threads");
        System.out.println("  ============================================================================" + RESET);
    }

    public static void printSection(String title) {
        System.out.println("\n" + PURPLE + BOLD + "--- [ " + title + " ] " + "-".repeat(Math.max(2, 60 - title.length())) + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "[SUCCESS] " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "[WARNING] " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "[ERROR] " + message + RESET);
    }
}
