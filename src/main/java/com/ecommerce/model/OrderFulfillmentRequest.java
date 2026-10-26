package com.ecommerce.model;

import java.time.LocalDateTime;

public class OrderFulfillmentRequest implements Comparable<OrderFulfillmentRequest> {

    private final Long orderId;
    private final String orderNumber;
    private final int priority;
    private final double orderAmount;
    private final LocalDateTime requestedAt;

    public OrderFulfillmentRequest(Long orderId, String orderNumber, int priority, double orderAmount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.priority = priority;
        this.orderAmount = orderAmount;
        this.requestedAt = LocalDateTime.now();
    }

    public Long getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public int getPriority() { return priority; }
    public double getOrderAmount() { return orderAmount; }
    public LocalDateTime getRequestedAt() { return requestedAt; }

    @Override
    public int compareTo(OrderFulfillmentRequest other) {
        if (other == null) return -1;
        int priorityComparison = Integer.compare(other.priority, this.priority);
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return this.requestedAt.compareTo(other.requestedAt);
    }
}