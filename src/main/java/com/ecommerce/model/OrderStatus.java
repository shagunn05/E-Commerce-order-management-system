package com.ecommerce.model;

public enum OrderStatus {
    PENDING(true, true),
    PROCESSING(true, false),
    SHIPPED(false, false),
    DELIVERED(false, false),
    CANCELLED(false, false);

    private final boolean canCancel;
    private final boolean canModifyItems;

    OrderStatus(boolean canCancel, boolean canModifyItems) {
        this.canCancel = canCancel;
        this.canModifyItems = canModifyItems;
    }

    public boolean canCancel() { return canCancel; }
    public boolean canModifyItems() { return canModifyItems; }
}