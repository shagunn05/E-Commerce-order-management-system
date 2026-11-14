package com.ecommerce.exception;

/**
 * Thrown when an illegal state transition is attempted on an Order
 * (e.g., trying to cancel an order that has already been delivered).
 */
public class InvalidOrderStateException extends EcommerceException {

    public InvalidOrderStateException(String orderNumber, String currentState, String requestedAction) {
        super("ERR_INVALID_ORDER_STATE",
                String.format("Cannot execute action '%s' on Order '%s' because its current state is '%s'.",
                        requestedAction, orderNumber, currentState));
    }
}
