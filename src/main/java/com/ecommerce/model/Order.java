
package com.ecommerce.model;

import com.ecommerce.exception.EcommerceException;
import com.ecommerce.exception.InvalidOrderStateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    private Long id;
    private String orderNumber;
    private Customer customer;
    private OrderStatus status;
    private final List<OrderItem> items = new ArrayList<>();
    private double totalAmount;
    private double discountApplied;
    private int priorityLevel;
    private LocalDateTime createdAt;

    public Order() {
        this.status = OrderStatus.PENDING;
        this.priorityLevel = 1;
        this.createdAt = LocalDateTime.now();
    }

    public Order(Long id, String orderNumber, Customer customer) {
        this.id = id;
        this.orderNumber = orderNumber;
        setCustomer(customer);
        this.status = OrderStatus.PENDING;
        this.priorityLevel = (customer != null && customer.getTier() != null) ? customer.getTier().getPriority() : 1;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        if (!this.status.canModifyItems()) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), "ADD_ITEM");
        }
        if (item == null) {
            throw new EcommerceException("Cannot add null item to Order.");
        }
        this.items.add(item);
        recalculateTotals();
    }

    public void removeItem(Long productId) {
        if (!this.status.canModifyItems()) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), "REMOVE_ITEM");
        }
        this.items.removeIf(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
        recalculateTotals();
    }

    public void recalculateTotals() {
        if (items.isEmpty()) {
            this.totalAmount = 0.0;
            this.discountApplied = 0.0;
            return;
        }

        double grossSubtotal = items.stream().mapToDouble(OrderItem::getSubtotal).sum();
        CustomerTier tier = (customer != null && customer.getTier() != null) ? customer.getTier() : CustomerTier.REGULAR;

        this.discountApplied = items.stream()
                .filter(item -> item.getProduct() != null)
                .mapToDouble(item -> item.getProduct().calculateDiscount(item.getQuantity(), tier))
                .sum();

        double shippingTotal = items.stream()
                .filter(item -> item.getProduct() != null)
                .mapToDouble(item -> item.getProduct().calculateShippingCost())
                .sum();

        double netAmount = Math.max(0.0, (grossSubtotal - this.discountApplied) + shippingTotal);
        this.totalAmount = Math.round(netAmount * 100.0) / 100.0;
        this.discountApplied = Math.round(this.discountApplied * 100.0) / 100.0;
    }

    public void cancel() {
        if (!this.status.canCancel()) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), "CANCEL");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void markProcessing() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), "PROCESS");
        }
        this.status = OrderStatus.PROCESSING;
    }

    public void markShipped() {
        if (this.status != OrderStatus.PROCESSING) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), "SHIP");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void markDelivered() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), "DELIVER");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null && customer.getTier() != null) {
            this.priorityLevel = customer.getTier().getPriority();
        }
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }

    public void setItems(List<OrderItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        recalculateTotals();
    }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(double discountApplied) { this.discountApplied = discountApplied; }
    public int getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(int priorityLevel) { this.priorityLevel = priorityLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}