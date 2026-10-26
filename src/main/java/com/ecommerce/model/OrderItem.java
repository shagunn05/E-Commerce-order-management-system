package com.ecommerce.model;

import com.ecommerce.exception.EcommerceException;

public class OrderItem {

    private Long id;
    private Long orderId;
    private Product product;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public OrderItem() {
    }

    public OrderItem(Long id, Long orderId, Product product, int quantity, double unitPrice) {
        this.id = id;
        this.orderId = orderId;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Product getProduct() { return product; }

    public void setProduct(Product product) {
        if (product == null) {
            throw new EcommerceException("Product cannot be null in an OrderItem.");
        }
        this.product = product;
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new EcommerceException("Order item quantity must be at least 1.");
        }
        this.quantity = quantity;
        recalculateSubtotal();
    }

    public double getUnitPrice() { return unitPrice; }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0.0) {
            throw new EcommerceException("Unit price cannot be negative.");
        }
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}