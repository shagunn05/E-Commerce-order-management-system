package com.ecommerce.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Lifecycle states of an Order with State Transition Rules.
 * Demonstrates Enum-based State Machine pattern and domain invariants.
 */
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

    public boolean canCancel() {
        return canCancel;
    }

    public boolean canModifyItems() {
        return canModifyItems;
    }

    /**
     * Validates if transition from current state to the candidate target state is legally allowed.
     */
    public boolean canTransitionTo(OrderStatus target) {
        if (target == null || target == this) return true;
        return switch (this) {
            case PENDING -> target == PROCESSING || target == CANCELLED;
            case PROCESSING -> target == SHIPPED || target == CANCELLED;
            case SHIPPED -> target == DELIVERED;
            case DELIVERED, CANCELLED -> false; // Terminal states
        };
    }
}
