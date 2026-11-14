package com.managementsystem.exception;

public class UserNotFoundException extends ManagementException {
    public UserNotFoundException(String userId) {
        super("ERR_USER_NOT_FOUND", "User with ID '" + userId + "' was not found in the system.");
    }
}
