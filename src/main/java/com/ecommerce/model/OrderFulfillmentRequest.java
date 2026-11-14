package com.ecommerce.model;

import java.time.LocalDateTime;

/**
 * Encapsulates an order fulfillment request to be dispatched through a
 * PriorityQueue (Java Collections Framework).
 * Implements Comparable to prioritize high-tier customers (Platinum > Gold > Silver > Regular)
 * and uses creation timestamp as a tie-breaker.
 */
public class OrderFulfillmentRequest implements Comparable<OrderFulfillmentRequest> {

    private final Long orderId;
    private final String orderNumber;
    private final int priority; // 4=Platinum, 3=Gold, 2=Silver, 1=Regular
    private final double orderAmount;
    private final LocalDateTime requestedAt;

    public OrderFulfillmentRequest(Long orderId, String orderNumber, int priority, double orderAmount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.priority = priority;
        this.orderAmount = orderAmount;
        this.requestedAt = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public int getPriority() {
        return priority;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    /**
     * Comparison logic for PriorityQueue:
     * 1. Higher priority number comes first (descending order).
     * 2. If priorities are identical, earlier timestamp comes first (ascending FIFO).
     */
    @Override
    public int compareTo(OrderFulfillmentRequest other) {
        if (other == null) return -1;

        int priorityComparison = Integer.compare(other.priority, this.priority);
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return this.requestedAt.compareTo(other.requestedAt);
    }

    @Override
    public String toString() {
        return String.format("FulfillmentJob[Order: %s, Priority: %d, Amount: $%.2f, QueuedAt: %s]",
                orderNumber, priority, orderAmount, requestedAt);
    }
}
