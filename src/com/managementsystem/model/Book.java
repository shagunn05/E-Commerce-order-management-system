package com.managementsystem.model;

import com.managementsystem.exception.InsufficientStockException;
import com.managementsystem.exception.ItemNotAvailableException;
import java.util.HashSet;
import java.util.Set;

/**
 * Concrete class demonstrating Inheritance and Polymorphism.
 * Extends Item and implements Borrowable interface.
 */
public class Book extends Item implements Borrowable {
    private String author;
    private String isbn;
    private String genre;
    private int totalPages;
    private final Set<String> currentBorrowers;
    private final int maxLoanDays;
    private final double finePerDay;

    public Book(String id, String title, String author, String isbn, String genre,
                double unitPrice, int totalCopies, int reorderThreshold, int maxLoanDays, double finePerDay) {
        super(id, title, "Books", unitPrice, totalCopies, reorderThreshold);
        this.author = author != null ? author.trim() : "Unknown Author";
        this.isbn = isbn != null ? isbn.trim() : "N/A";
        this.genre = genre != null ? genre.trim() : "General";
        this.totalPages = 300;
        this.currentBorrowers = new HashSet<>();
        this.maxLoanDays = maxLoanDays > 0 ? maxLoanDays : 14;
        this.finePerDay = finePerDay >= 0 ? finePerDay : 1.50;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public boolean canBeBorrowed() {
        getItemLock().lock();
        try {
            return getStockQuantity() > 0;
        } finally {
            getItemLock().unlock();
        }
    }

    @Override
    public void borrowCopy(String memberId) throws ItemNotAvailableException, InsufficientStockException {
        getItemLock().lock();
        try {
            if (currentBorrowers.contains(memberId)) {
                throw new ItemNotAvailableException(getId(), "Member " + memberId + " already holds a borrowed copy of this book.");
            }
            if (getStockQuantity() <= 0) {
                throw new InsufficientStockException(getId(), 1, 0);
            }
            deductStock(1);
            currentBorrowers.add(memberId);
        } finally {
            getItemLock().unlock();
        }
    }

    @Override
    public void returnCopy(String memberId) throws ItemNotAvailableException {
        getItemLock().lock();
        try {
            if (!currentBorrowers.contains(memberId)) {
                throw new ItemNotAvailableException(getId(), "Member " + memberId + " does not have this book borrowed.");
            }
            currentBorrowers.remove(memberId);
            addStock(1);
        } finally {
            getItemLock().unlock();
        }
    }

    @Override
    public int getAvailableCopies() {
        return getStockQuantity();
    }

    @Override
    public int getBorrowedCopies() {
        getItemLock().lock();
        try {
            return currentBorrowers.size();
        } finally {
            getItemLock().unlock();
        }
    }

    @Override
    public int getMaxLoanDays() {
        return maxLoanDays;
    }

    @Override
    public double calculateFine(long overdueDays) {
        if (overdueDays <= 0) return 0.0;
        return overdueDays * finePerDay;
    }

    public double getFinePerDay() {
        return finePerDay;
    }

    @Override
    public String getItemType() {
        return "BOOK";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("Author: %s | ISBN: %s | Genre: %s | Loan Period: %d days | Fine/day: $%.2f | Borrowed: %d",
                author, isbn, genre, maxLoanDays, finePerDay, getBorrowedCopies());
    }

    @Override
    public boolean matches(String query) {
        if (super.matches(query)) return true;
        if (query == null) return false;
        String q = query.toLowerCase();
        return author.toLowerCase().contains(q) ||
               isbn.toLowerCase().contains(q) ||
               genre.toLowerCase().contains(q);
    }
}
