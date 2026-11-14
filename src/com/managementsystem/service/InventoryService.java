package com.managementsystem.service;

import com.managementsystem.concurrent.AsyncAuditLogger;
import com.managementsystem.exception.DuplicateItemException;
import com.managementsystem.exception.InsufficientStockException;
import com.managementsystem.exception.ItemNotFoundException;
import com.managementsystem.model.Item;
import com.managementsystem.model.Transaction;
import com.managementsystem.model.TransactionType;
import com.managementsystem.repository.Repository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service managing stock inventory operations.
 * Demonstrates Collections (Streams, Lists), Exception Handling, and Thread synchronization.
 */
public class InventoryService {
    private final Repository<Item, String> itemRepository;
    private final Repository<Transaction, String> transactionRepository;
    private final AsyncAuditLogger auditLogger;

    public InventoryService(Repository<Item, String> itemRepository,
                            Repository<Transaction, String> transactionRepository,
                            AsyncAuditLogger auditLogger) {
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogger = auditLogger;
    }

    public void addItem(Item item) throws DuplicateItemException {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (itemRepository.existsById(item.getId())) {
            throw new DuplicateItemException(item.getId());
        }
        itemRepository.save(item);

        Transaction tx = new Transaction(
                "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                TransactionType.RESTOCK_INVENTORY,
                "SYSTEM",
                item.getId(),
                item.getStockQuantity(),
                item.getUnitPrice() * item.getStockQuantity(),
                "Initial catalog addition of " + item.getTitle()
        );
        recordTransaction(tx);
    }

    public Item getItem(String itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public void issueStock(String itemId, int quantity, String userId, String reason)
            throws ItemNotFoundException, InsufficientStockException {
        Item item = getItem(itemId);

        // Thread-safe deduction using item lock
        item.getItemLock().lock();
        try {
            item.deductStock(quantity);
            Transaction tx = new Transaction(
                    "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    TransactionType.ISSUE_INVENTORY,
                    userId,
                    itemId,
                    quantity,
                    item.getUnitPrice() * quantity,
                    "Stock issued: " + reason
            );
            recordTransaction(tx);
        } finally {
            item.getItemLock().unlock();
        }
    }

    public void restock(String itemId, int quantity, String staffId) throws ItemNotFoundException {
        Item item = getItem(itemId);

        // Thread-safe replenishment using item lock
        item.getItemLock().lock();
        try {
            item.addStock(quantity);
            Transaction tx = new Transaction(
                    "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    TransactionType.RESTOCK_INVENTORY,
                    staffId,
                    itemId,
                    quantity,
                    item.getUnitPrice() * quantity,
                    "Restocked " + quantity + " units by staff " + staffId
            );
            recordTransaction(tx);
        } finally {
            item.getItemLock().unlock();
        }
    }

    public List<Item> search(String query) {
        return itemRepository.findBy(item -> item.matches(query));
    }

    public List<Item> getByCategory(String category) {
        return itemRepository.findBy(item -> item.getCategory().equalsIgnoreCase(category));
    }

    public List<Item> getLowStockItems() {
        return itemRepository.findBy(Item::isLowStock);
    }

    public double calculateTotalValuation() {
        return itemRepository.findAll().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getStockQuantity())
                .sum();
    }

    private void recordTransaction(Transaction tx) {
        transactionRepository.save(tx);
        if (auditLogger != null) {
            auditLogger.logTransaction(tx);
        }
    }
}
