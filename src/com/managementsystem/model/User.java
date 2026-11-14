package com.managementsystem.model;

import java.util.Objects;

/**
 * Abstract class representing a user in the system.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class User {
    private final String userId;
    private String name;
    private String email;

    public User(String userId, String name, String email) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        this.userId = userId.trim();
        this.name = name != null ? name.trim() : "Unknown User";
        this.email = email != null ? email.trim() : "no-email@domain.com";
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            this.email = email.trim();
        }
    }

    public abstract String getRole();
    public abstract String getUserSummary();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %-6s | Name: %-18s | Email: %s", getRole(), userId, name, email);
    }
}
