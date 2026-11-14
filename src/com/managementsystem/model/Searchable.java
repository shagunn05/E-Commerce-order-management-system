package com.managementsystem.model;

/**
 * Interface demonstrating Abstraction for items that can be searched.
 */
public interface Searchable {
    boolean matches(String query);
}
