package com.managementsystem.service;

import com.managementsystem.concurrent.AsyncAuditLogger;
import com.managementsystem.exception.InsufficientStockException;
import com.managementsystem.exception.InvalidOperationException;
import com.managementsystem.exception.ItemNotAvailableException;
import com.managementsystem.exception.ItemNotFoundException;
import com.managementsystem.exception.MaxBorrowLimitExceededException;
import com.managementsystem.exception.UserNotFoundException;
import com.managementsystem.model.Book;
import com.managementsystem.model.BorrowRecord;
import com.managementsystem.model.Item;
import com.managementsystem.model.Member;
import com.managementsystem.model.ReservationRequest;
import com.managementsystem.model.Transaction;
import com.managementsystem.model.TransactionType;
import com.managementsystem.model.User;
import com.managementsystem.repository.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service managing library operations: borrowing, returns, fines, and reservations.
 * Demonstrates Collections (PriorityQueue, Lists, ConcurrentHashMap), OOP (Polymorphism),
 * and Exception handling.
 */
public class LibraryService {
    private final Repository<Item, String> itemRepository;
    private final Repository<User, String> userRepository;
    private final Repository<Transaction, String> transactionRepository;
    private final AsyncAuditLogger auditLogger;

    // Advanced collection: PriorityQueue per book for waiting list reservations
    private final Map<String, PriorityQueue<ReservationRequest>> reservationQueues;

    public LibraryService(Repository<Item, String> itemRepository,
                          Repository<User, String> userRepository,
                          Repository<Transaction, String> transactionRepository,
                          AsyncAuditLogger auditLogger) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogger = auditLogger;
        this.reservationQueues = new ConcurrentHashMap<>();
    }

    public BorrowRecord borrowBook(String memberId, String bookId)
            throws UserNotFoundException, ItemNotFoundException, ItemNotAvailableException,
                   InsufficientStockException, MaxBorrowLimitExceededException, InvalidOperationException {

        // 1. Validate User
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException(memberId));
        if (!(user instanceof Member member)) {
            throw new InvalidOperationException("User '" + memberId + "' is not a registered Member (patron).");
        }

        // 2. Validate Book
        Item item = itemRepository.findById(bookId)
                .orElseThrow(() -> new ItemNotFoundException(bookId));
        if (!(item instanceof Book book)) {
            throw new InvalidOperationException("Item '" + bookId + "' is an inventory asset, not a borrowable Book.");
        }

        // 3. Thread-safe Borrow Transaction
        book.getItemLock().lock();
        try {
            // Check availability and deduct stock
            book.borrowCopy(memberId);

            // Create BorrowRecord
            String recordId = "REC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            BorrowRecord record = new BorrowRecord(
                    recordId, member.getUserId(), book.getId(), book.getTitle(),
                    LocalDate.now(), book.getMaxLoanDays()
            );

            // Record into member's active loans
            member.addBorrowRecord(record);

            // Audit transaction
            Transaction tx = new Transaction(
                    "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    TransactionType.BORROW_BOOK,
                    memberId,
                    bookId,
                    1,
                    0.0,
                    "Loan recorded. Due date: " + record.getDueDate()
            );
            recordTransaction(tx);

            return record;
        } finally {
            book.getItemLock().unlock();
        }
    }

    public double returnBook(String memberId, String bookId, LocalDate returnDate)
            throws UserNotFoundException, ItemNotFoundException, InvalidOperationException, ItemNotAvailableException {

        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException(memberId));
        if (!(user instanceof Member member)) {
            throw new InvalidOperationException("User '" + memberId + "' is not a registered Member.");
        }

        Item item = itemRepository.findById(bookId)
                .orElseThrow(() -> new ItemNotFoundException(bookId));
        if (!(item instanceof Book book)) {
            throw new InvalidOperationException("Item '" + bookId + "' is not a Book.");
        }

        book.getItemLock().lock();
        try {
            // Find active borrow record for this book
            BorrowRecord targetRecord = member.getActiveBorrows().stream()
                    .filter(r -> r.getItemId().equalsIgnoreCase(bookId))
                    .findFirst()
                    .orElseThrow(() -> new InvalidOperationException("No active loan found for member " + memberId + " and book " + bookId));

            // Return copy back to book
            book.returnCopy(memberId);

            // Calculate fine if overdue
            LocalDate actualDate = returnDate != null ? returnDate : LocalDate.now();
            long overdueDays = targetRecord.getOverdueDays(actualDate);
            double fine = book.calculateFine(overdueDays);

            targetRecord.markReturned(actualDate, fine);

            if (fine > 0) {
                member.addFine(fine);
            }

            // Check if there are waitlisted reservations for this book
            notifyNextInReservationQueue(bookId);

            // Audit transaction
            Transaction tx = new Transaction(
                    "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    TransactionType.RETURN_BOOK,
                    memberId,
                    bookId,
                    1,
                    fine,
                    "Book returned. Overdue days: " + overdueDays + ", Fine assessed: $" + String.format("%.2f", fine)
            );
            recordTransaction(tx);

            return fine;
        } finally {
            book.getItemLock().unlock();
        }
    }

    public void placeReservation(String memberId, String bookId, int priorityTier)
            throws UserNotFoundException, ItemNotFoundException, InvalidOperationException {

        if (!userRepository.existsById(memberId)) {
            throw new UserNotFoundException(memberId);
        }
        Item item = itemRepository.findById(bookId)
                .orElseThrow(() -> new ItemNotFoundException(bookId));
        if (!(item instanceof Book)) {
            throw new InvalidOperationException("Item '" + bookId + "' is not a Book and cannot be reserved.");
        }

        reservationQueues.computeIfAbsent(bookId, k -> new PriorityQueue<>());
        PriorityQueue<ReservationRequest> queue = reservationQueues.get(bookId);

        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        ReservationRequest request = new ReservationRequest(reservationId, memberId, bookId, priorityTier);

        synchronized (queue) {
            queue.offer(request);
        }

        Transaction tx = new Transaction(
                "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                TransactionType.RESERVATION_PLACED,
                memberId,
                bookId,
                1,
                0.0,
                "Reservation queued (Tier: " + priorityTier + ", ID: " + reservationId + ")"
        );
        recordTransaction(tx);
    }

    private void notifyNextInReservationQueue(String bookId) {
        PriorityQueue<ReservationRequest> queue = reservationQueues.get(bookId);
        if (queue != null) {
            synchronized (queue) {
                ReservationRequest next = queue.poll();
                if (next != null) {
                    System.out.printf("\n[RESERVATION NOTIFICATION] Book '%s' is now available! Next in priority queue: Member %s (Priority Tier: %d)\n",
                            bookId, next.getMemberId(), next.getPriorityTier());
                }
            }
        }
    }

    public double payFine(String memberId, double amount) throws UserNotFoundException, InvalidOperationException {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException(memberId));
        if (!(user instanceof Member member)) {
            throw new InvalidOperationException("User is not a Member.");
        }

        double change = member.payFine(amount);

        Transaction tx = new Transaction(
                "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                TransactionType.FINE_PAYMENT,
                memberId,
                "FINES",
                1,
                amount - change,
                "Fine payment received. Remaining unpaid: $" + String.format("%.2f", member.getUnpaidFines())
        );
        recordTransaction(tx);

        return change;
    }

    public List<Book> getAllBooks() {
        return itemRepository.findAll().stream()
                .filter(i -> i instanceof Book)
                .map(i -> (Book) i)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> getMemberActiveLoans(String memberId) throws UserNotFoundException, InvalidOperationException {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException(memberId));
        if (!(user instanceof Member member)) {
            throw new InvalidOperationException("User is not a Member.");
        }
        return member.getActiveBorrows();
    }

    private void recordTransaction(Transaction tx) {
        transactionRepository.save(tx);
        if (auditLogger != null) {
            auditLogger.logTransaction(tx);
        }
    }
}
