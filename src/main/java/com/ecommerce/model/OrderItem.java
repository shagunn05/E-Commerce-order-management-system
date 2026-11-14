package com.ecommerce.model;

import com.ecommerce.exception.EcommerceException;
import jakarta.persistence.*;

/**
 * OrderItem JPA Entity representing a line item in an Order.
 * Demonstrates:
 * - Hibernate ORM: @ManyToOne relationships with lazy and eager loading
 * - Encapsulation and precision price calculations
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private double subtotal;

    public OrderItem() {
    }

    public OrderItem(Order order, Product product, int quantity, double unitPrice) {
        this.order = order;
        setProduct(product);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
        recalculateSubtotal();
    }

    public OrderItem(Product product, int quantity) {
        setProduct(product);
        setQuantity(quantity);
        setUnitPrice(product.getPrice());
        recalculateSubtotal();
    }

    public void recalculateSubtotal() {
        this.subtotal = Math.round((this.unitPrice * this.quantity) * 100.0) / 100.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new EcommerceException("Product cannot be null in an OrderItem.");
        }
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new EcommerceException("Order item quantity must be at least 1.");
        }
        this.quantity = quantity;
        recalculateSubtotal();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0.0) {
            throw new EcommerceException("Unit price cannot be negative.");
        }
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return String.format("%s x %d @ $%.2f = $%.2f",
                (product != null ? product.getName() : "Item"), quantity, unitPrice, subtotal);
    }
}
