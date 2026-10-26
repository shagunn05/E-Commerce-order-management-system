package com.ecommerce.exception;

public class InvalidOrderStateException extends EcommerceException {
    public InvalidOrderStateException(String orderNumber, String currentState, String requestedAction) {
        super("ERR_INVALID_ORDER_STATE",
                String.format("Cannot execute action '%s' on Order '%s' because its current state is '%s'.",
                        requestedAction, orderNumber, currentState));
    }
}