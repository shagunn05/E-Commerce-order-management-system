package com.managementsystem.exception;

public class ItemNotAvailableException extends ManagementException {
    public ItemNotAvailableException(String itemId, String reason) {
        super("ERR_ITEM_UNAVAILABLE", "Item '" + itemId + "' is currently unavailable: " + reason);
    }
}
