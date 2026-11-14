package com.managementsystem.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Record holding information about an active or past loan of a library item.
 */
public class BorrowRecord {
    private final String recordId;
    private final String memberId;
    private final String itemId;
    private final String itemTitle;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAssessed;
    private boolean isReturned;

    public BorrowRecord(String recordId, String memberId, String itemId, String itemTitle,
                        LocalDate borrowDate, int loanDays) {
        this.recordId = recordId;
        this.memberId = memberId;
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.borrowDate = borrowDate != null ? borrowDate : LocalDate.now();
        this.dueDate = this.borrowDate.plusDays(loanDays);
        this.isReturned = false;
        this.fineAssessed = 0.0;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public double getFineAssessed() {
        return fineAssessed;
    }

    public void markReturned(LocalDate actualReturnDate, double fine) {
        this.isReturned = true;
        this.returnDate = actualReturnDate;
        this.fineAssessed = fine;
    }

    public boolean isOverdue(LocalDate currentDate) {
        if (isReturned) return false;
        return currentDate.isAfter(dueDate);
    }

    public long getOverdueDays(LocalDate currentDate) {
        if (!isOverdue(currentDate)) return 0;
        return ChronoUnit.DAYS.between(dueDate, currentDate);
    }

    @Override
    public String toString() {
        return String.format("[%s] Item: %s (%s) | Borrowed: %s | Due: %s | Status: %s",
                recordId, itemTitle, itemId, borrowDate, dueDate, isReturned ? "Returned" : "ACTIVE");
    }
}
