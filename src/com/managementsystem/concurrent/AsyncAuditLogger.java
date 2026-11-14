package com.managementsystem.concurrent;

import com.managementsystem.model.Transaction;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Worker thread that logs system transactions asynchronously via a BlockingQueue.
 * Demonstrates: Producer-Consumer pattern, BlockingQueue, and Thread lifecycle management.
 */
public class AsyncAuditLogger implements Runnable {
    private final BlockingQueue<Transaction> queue;
    private final AtomicBoolean running;
    private final String logFilePath;
    private int loggedCount;

    public AsyncAuditLogger(String logFilePath) {
        this.queue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(true);
        this.logFilePath = logFilePath != null ? logFilePath : "system_audit.log";
        this.loggedCount = 0;
        initFile();
    }

    private void initFile() {
        try {
            Path path = Paths.get(logFilePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            System.err.println("[AsyncAuditLogger] Failed to create log directory: " + e.getMessage());
        }
    }

    public void logTransaction(Transaction tx) {
        if (tx != null && running.get()) {
            queue.offer(tx);
        }
    }

    @Override
    public void run() {
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] AsyncAuditLogger started.");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            while (running.get() || !queue.isEmpty()) {
                try {
                    Transaction tx = queue.poll(500, TimeUnit.MILLISECONDS);
                    if (tx != null) {
                        String record = tx.toString();
                        writer.write(record);
                        writer.newLine();
                        writer.flush();
                        loggedCount++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[AsyncAuditLogger] File I/O Error: " + e.getMessage());
        }
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] AsyncAuditLogger stopped. Total logged: " + loggedCount);
    }

    public void stop() {
        running.set(false);
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getLoggedCount() {
        return loggedCount;
    }
}
