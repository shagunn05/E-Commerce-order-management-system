package com.managementsystem.concurrent;

import com.managementsystem.model.Item;
import com.managementsystem.repository.Repository;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Background worker thread that periodically inspects inventory stock
 * and triggers reorder warnings when levels fall below threshold.
 * Demonstrates: Thread sleeping, atomic flags, and periodic task execution.
 */
public class StockMonitorWorker implements Runnable {
    private final Repository<Item, String> itemRepository;
    private final AtomicBoolean running;
    private final long checkIntervalMs;

    public StockMonitorWorker(Repository<Item, String> itemRepository, long checkIntervalMs) {
        this.itemRepository = itemRepository;
        this.running = new AtomicBoolean(true);
        this.checkIntervalMs = checkIntervalMs > 0 ? checkIntervalMs : 5000;
    }

    @Override
    public void run() {
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] StockMonitorWorker started (Interval: " + (checkIntervalMs / 1000) + "s).");
        while (running.get()) {
            try {
                checkStockLevels();
                Thread.sleep(checkIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] StockMonitorWorker stopped.");
    }

    private void checkStockLevels() {
        List<Item> lowStockItems = itemRepository.findBy(Item::isLowStock);
        if (!lowStockItems.isEmpty()) {
            System.out.println("\n[ALERT - StockMonitorWorker] Detected " + lowStockItems.size() + " item(s) below reorder threshold:");
            for (Item item : lowStockItems) {
                System.out.printf("  >> [LOW STOCK ALERT] %s (ID: %s) -> Current Stock: %d | Threshold: %d\n",
                        item.getTitle(), item.getId(), item.getStockQuantity(), item.getReorderThreshold());
            }
        }
    }

    public void stop() {
        running.set(false);
    }
}
