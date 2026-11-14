package com.ecommerce.model;

import com.ecommerce.exception.EcommerceException;
import com.ecommerce.exception.InvalidOrderStateException;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Order Domain Aggregate Root & JPA Entity.
 * Demonstrates:
 * - Hibernate ORM: @Entity, @Table, @OneToMany(cascade=ALL, orphanRemoval=true), @ManyToOne, @Version
 * - Concurrency: Optimistic Locking with @Version prevents lost updates
 * - Business Rules & Invariants: State machine transition checks and Java Streams calculation pipelines
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 60)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OrderStatus status = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "subtotal_amount", nullable = false)
    private double subtotalAmount = 0.0;

    @Column(name = "discount_applied", nullable = false)
    private double discountApplied = 0.0;

    @Column(name = "shipping_fee", nullable = false)
    private double shippingFee = 0.0;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount = 0.0;

    @Column(name = "priority_level", nullable = false)
    private int priorityLevel = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version;

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

    /**
     * Domain Helper for bidirectional relationship sync and total recalculation.
     */
    public void addItem(OrderItem item) {
        if (!this.status.canModifyItems()) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), "ADD_ITEM");
        }
        if (item == null) {
            throw new EcommerceException("Cannot add null item to Order.");
        }
        item.setOrder(this);
        this.items.add(item);
        recalculateTotals();
    }

    public void removeItem(OrderItem item) {
        if (!this.status.canModifyItems()) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), "REMOVE_ITEM");
        }
        if (item != null && this.items.remove(item)) {
            item.setOrder(null);
            recalculateTotals();
        }
    }

    /**
     * Recalculates gross subtotal, polymorphic discounts, and shipping fee using Java Streams.
     */
    public void recalculateTotals() {
        if (items.isEmpty()) {
            this.subtotalAmount = 0.0;
            this.discountApplied = 0.0;
            this.shippingFee = 0.0;
            this.totalAmount = 0.0;
            return;
        }

        // 1. Gross Subtotal using Streams API
        this.subtotalAmount = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();

        // 2. Polymorphic item & customer tier discounts
        CustomerTier tier = (customer != null && customer.getTier() != null) ? customer.getTier() : CustomerTier.REGULAR;

        this.discountApplied = items.stream()
                .filter(item -> item.getProduct() != null)
                .mapToDouble(item -> item.getProduct().calculateDiscount(item.getQuantity(), tier))
                .sum();

        // 3. Shipping fee via polymorphism (free if subtotal exceeds $100 threshold)
        double calculatedShipping = items.stream()
                .filter(item -> item.getProduct() != null)
                .mapToDouble(item -> item.getProduct().calculateShippingCost())
                .sum();

        this.shippingFee = (this.subtotalAmount >= 100.00) ? 0.0 : calculatedShipping;

        // 4. Net Total
        double net = (this.subtotalAmount - this.discountApplied) + this.shippingFee;
        this.totalAmount = Math.max(0.0, Math.round(net * 100.0) / 100.0);
    }

    /**
     * Domain State Transition enforcing state machine rules.
     */
    public void transitionTo(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new EcommerceException("Target OrderStatus cannot be null.");
        }
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStateException(this.orderNumber, this.status.name(), newStatus.name());
        }
        this.status = newStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null && customer.getTier() != null) {
            this.priorityLevel = customer.getTier().getPriority();
        }
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<OrderItem> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(this::addItem);
        }
    }

    public double getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public double getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(double discountApplied) {
        this.discountApplied = discountApplied;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(orderNumber, order.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber);
    }

    @Override
    public String toString() {
        return String.format("Order[%s, Customer=%s, Status=%s, Items=%d, Total=$%.2f]",
                orderNumber, (customer != null ? customer.getName() : "N/A"), status, items.size(), totalAmount);
    }
}
