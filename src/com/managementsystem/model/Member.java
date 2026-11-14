package com.managementsystem.model;

import com.managementsystem.exception.MaxBorrowLimitExceededException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Concrete Member class representing patrons of the library/inventory system.
 */
public class Member extends User {
    private final int maxBorrowLimit;
    private final List<BorrowRecord> borrowHistory;
    private double unpaidFines;

    public Member(String userId, String name, String email, int maxBorrowLimit) {
        super(userId, name, email);
        this.maxBorrowLimit = maxBorrowLimit > 0 ? maxBorrowLimit : 3;
        this.borrowHistory = new CopyOnWriteArrayList<>();
        this.unpaidFines = 0.0;
    }

    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }

    public synchronized List<BorrowRecord> getActiveBorrows() {
        List<BorrowRecord> active = new ArrayList<>();
        for (BorrowRecord record : borrowHistory) {
            if (!record.isReturned()) {
                active.add(record);
            }
        }
        return Collections.unmodifiableList(active);
    }

    public List<BorrowRecord> getAllBorrowHistory() {
        return Collections.unmodifiableList(borrowHistory);
    }

    public synchronized void addBorrowRecord(BorrowRecord record) throws MaxBorrowLimitExceededException {
        if (getActiveBorrows().size() >= maxBorrowLimit) {
            throw new MaxBorrowLimitExceededException(getUserId(), maxBorrowLimit);
        }
        borrowHistory.add(record);
    }

    public synchronized void addFine(double amount) {
        if (amount > 0) {
            this.unpaidFines += amount;
        }
    }

    public synchronized double payFine(double amount) {
        if (amount <= 0) return unpaidFines;
        if (amount >= unpaidFines) {
            double change = amount - unpaidFines;
            unpaidFines = 0.0;
            return change;
        } else {
            unpaidFines -= amount;
            return 0.0;
        }
    }

    public synchronized double getUnpaidFines() {
        return unpaidFines;
    }

    @Override
    public String getRole() {
        return "MEMBER";
    }

    @Override
    public String getUserSummary() {
        return String.format("Borrow Limit: %d | Active Loans: %d | Unpaid Fines: $%.2f",
                maxBorrowLimit, getActiveBorrows().size(), unpaidFines);
    }
}
