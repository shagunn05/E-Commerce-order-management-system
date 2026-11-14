package com.managementsystem.model;

import com.managementsystem.exception.InsufficientStockException;
import com.managementsystem.exception.ItemNotAvailableException;

/**
 * Interface demonstrating Abstraction for library items that can be borrowed and returned.
 */
public interface Borrowable {
    boolean canBeBorrowed();
    void borrowCopy(String memberId) throws ItemNotAvailableException, InsufficientStockException;
    void returnCopy(String memberId) throws ItemNotAvailableException;
    int getAvailableCopies();
    int getBorrowedCopies();
    int getMaxLoanDays();
    double calculateFine(long overdueDays);
}
