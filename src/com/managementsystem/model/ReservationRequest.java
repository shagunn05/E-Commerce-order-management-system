package com.managementsystem.model;

import java.time.LocalDateTime;

/**
 * Reservation record stored in a PriorityQueue to illustrate advanced Collection usage.
 * Priority is determined by Member tier (higher tier first) then earliest request timestamp.
 */
public class ReservationRequest implements Comparable<ReservationRequest> {
    private final String reservationId;
    private final String memberId;
    private final String bookId;
    private final LocalDateTime requestedAt;
    private final int priorityTier; // e.g., 1 = Faculty/VIP, 2 = Student, 3 = Guest

    public ReservationRequest(String reservationId, String memberId, String bookId, int priorityTier) {
        this.reservationId = reservationId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.priorityTier = priorityTier;
        this.requestedAt = LocalDateTime.now();
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getBookId() {
        return bookId;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public int getPriorityTier() {
        return priorityTier;
    }

    @Override
    public int compareTo(ReservationRequest other) {
        // Lower number = higher priority
        if (this.priorityTier != other.priorityTier) {
            return Integer.compare(this.priorityTier, other.priorityTier);
        }
        return this.requestedAt.compareTo(other.requestedAt);
    }

    @Override
    public String toString() {
        return String.format("[%s] Member: %s | Book: %s | Priority Tier: %d | Requested: %s",
                reservationId, memberId, bookId, priorityTier, requestedAt);
    }
}
