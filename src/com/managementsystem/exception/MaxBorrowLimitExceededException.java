package com.managementsystem.exception;

public class MaxBorrowLimitExceededException extends ManagementException {
    public MaxBorrowLimitExceededException(String userId, int limit) {
        super("ERR_LIMIT_EXCEEDED", "User '" + userId + "' has reached the maximum borrowing limit of " + limit + " items.");
    }
}
