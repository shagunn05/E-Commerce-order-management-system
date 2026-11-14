package com.managementsystem.concurrent;

import com.managementsystem.model.BorrowRecord;
import com.managementsystem.model.Member;
import com.managementsystem.model.User;
import com.managementsystem.repository.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Background worker thread that scans members' active borrows, calculates overdue fines,
 * and prints reminder notices.
 * Demonstrates: Scheduled background processing and thread synchronization.
 */
public class OverdueNotificationWorker implements Runnable {
    private final Repository<User, String> userRepository;
    private final AtomicBoolean running;
    private final long checkIntervalMs;

    public OverdueNotificationWorker(Repository<User, String> userRepository, long checkIntervalMs) {
        this.userRepository = userRepository;
        this.running = new AtomicBoolean(true);
        this.checkIntervalMs = checkIntervalMs > 0 ? checkIntervalMs : 8000;
    }

    @Override
    public void run() {
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] OverdueNotificationWorker started (Interval: " + (checkIntervalMs / 1000) + "s).");
        while (running.get()) {
            try {
                scanOverdueBorrows();
                Thread.sleep(checkIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("[THREAD " + Thread.currentThread().getName() + "] OverdueNotificationWorker stopped.");
    }

    private void scanOverdueBorrows() {
        LocalDate today = LocalDate.now();
        List<User> members = userRepository.findBy(u -> u instanceof Member);
        for (User u : members) {
            Member m = (Member) u;
            for (BorrowRecord record : m.getActiveBorrows()) {
                if (record.isOverdue(today)) {
                    long overdueDays = record.getOverdueDays(today);
                    System.out.printf("\n[OVERDUE NOTICE] Member: %s (ID: %s) | Book: '%s' is overdue by %d day(s)! Due date was: %s\n",
                            m.getName(), m.getUserId(), record.getItemTitle(), overdueDays, record.getDueDate());
                }
            }
        }
    }

    public void stop() {
        running.set(false);
    }
}
