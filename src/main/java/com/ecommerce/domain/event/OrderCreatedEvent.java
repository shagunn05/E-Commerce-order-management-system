package com.ecommerce.domain.event;

import java.time.LocalDateTime;

public class OrderCreatedEvent {

    private final Long orderId;
    private final String orderNumber;
    private final Long customerId;
    private final String customerEmail;
    private final String customerName;
    private final double totalAmount;
    private final int itemsCount;
    private final LocalDateTime eventTimestamp;

    public OrderCreatedEvent(Long orderId, String orderNumber, Long customerId,
                             String customerEmail, String customerName,
                             double totalAmount, int itemsCount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.itemsCount = itemsCount;
        this.eventTimestamp = LocalDateTime.now();
    }

    public Long getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
    public int getItemsCount() { return itemsCount; }
    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
}