package com.managementsystem.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Encapsulates an audit record for every action in the system.
 */
public class Transaction {
    private final String transactionId;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final String userId;
    private final String itemId;
    private final int quantity;
    private final double amount;
    private final String notes;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(String transactionId, TransactionType type, String userId,
                       String itemId, int quantity, double amount, String notes) {
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.userId = userId != null ? userId : "SYSTEM";
        this.itemId = itemId != null ? itemId : "N/A";
        this.quantity = quantity;
        this.amount = amount;
        this.notes = notes != null ? notes : "";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAmount() {
        return amount;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Type: %-18s | User: %-7s | Item: %-7s | Qty: %-3d | Amt: $%-6.2f | %s",
                transactionId, timestamp.format(FORMATTER), type, userId, itemId, quantity, amount, notes);
    }
}
