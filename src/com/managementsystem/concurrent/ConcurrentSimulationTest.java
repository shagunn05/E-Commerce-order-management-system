package com.managementsystem.concurrent;

import com.managementsystem.exception.ManagementException;
import com.managementsystem.model.Book;
import com.managementsystem.model.Member;
import com.managementsystem.service.LibraryService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Concurrency Test & Demonstration Suite.
 * Simulates high-concurrency race condition scenarios (e.g., 10 simultaneous users
 * competing for only 3 available book copies) using ExecutorService and CountDownLatch.
 * Proves that locks and synchronization protect data integrity.
 */
public class ConcurrentSimulationTest {
    private final LibraryService libraryService;

    public ConcurrentSimulationTest(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    public void runSimulation(Book targetBook, Member[] testMembers) {
        System.out.println("\n========================================================================");
        System.out.println(">>> STARTING MULTI-THREADED CONCURRENT STRESS TEST");
        System.out.println("========================================================================");
        System.out.printf("Target Item: '%s' (ID: %s) | Total Available Stock: %d\n",
                targetBook.getTitle(), targetBook.getId(), targetBook.getStockQuantity());
        System.out.printf("Simultaneous Threads: %d competing patrons\n", testMembers.length);
        System.out.println("------------------------------------------------------------------------");

        int initialStock = targetBook.getStockQuantity();
        int threadCount = testMembers.length;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successfulBorrows = new AtomicInteger(0);
        AtomicInteger failedBorrows = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final Member member = testMembers[i];
            final int patronIndex = i + 1;

            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    // Wait for all threads to be ready so they fire at the EXACT same millisecond
                    startSignal.await();

                    System.out.printf("[Thread-%d] Patron %s attempting to borrow book %s...\n",
                            patronIndex, member.getUserId(), targetBook.getId());

                    libraryService.borrowBook(member.getUserId(), targetBook.getId());

                    successfulBorrows.incrementAndGet();
                    System.out.printf("  [SUCCESS] [Thread-%d] Patron %s successfully acquired a copy!\n",
                            patronIndex, member.getUserId());

                } catch (ManagementException e) {
                    failedBorrows.incrementAndGet();
                    System.out.printf("  [BLOCKED] [Thread-%d] Patron %s rejected: %s\n",
                            patronIndex, member.getUserId(), e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        try {
            // Wait for all worker threads to reach starting line
            readyLatch.await();
            System.out.println(">>> All threads synchronized at starting gate. Releasing starting signal NOW!");
            long startTime = System.currentTimeMillis();
            startSignal.countDown(); // FIRE!

            // Wait for all threads to complete
            doneLatch.await(5, TimeUnit.SECONDS);
            long elapsed = System.currentTimeMillis() - startTime;

            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS);

            System.out.println("------------------------------------------------------------------------");
            System.out.println(">>> CONCURRENCY TEST SUMMARY:");
            System.out.printf("  - Initial Stock:              %d\n", initialStock);
            System.out.printf("  - Total Concurrent Requests:  %d\n", threadCount);
            System.out.printf("  - Successful Checkouts:       %d\n", successfulBorrows.get());
            System.out.printf("  - Gracefully Rejected (Safe): %d\n", failedBorrows.get());
            System.out.printf("  - Final Remaining Stock:      %d\n", targetBook.getStockQuantity());
            System.out.printf("  - Time Taken:                 %d ms\n", elapsed);

            if (successfulBorrows.get() == Math.min(initialStock, threadCount) &&
                targetBook.getStockQuantity() == Math.max(0, initialStock - successfulBorrows.get())) {
                System.out.println("\n[PASSED] DATA INTEGRITY VERIFIED: Zero race conditions! No overselling occurred.");
            } else {
                System.out.println("\n[FAILED] Inconsistency detected!");
            }
            System.out.println("========================================================================\n");

        } catch (InterruptedException e) {
            System.err.println("Simulation interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
