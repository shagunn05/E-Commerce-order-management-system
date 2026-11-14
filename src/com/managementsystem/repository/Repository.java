package com.managementsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Generic repository interface demonstrating Java Generics & Collection Abstraction.
 *
 * @param <T>  Entity type
 * @param <ID> Unique identifier type
 */
public interface Repository<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean existsById(ID id);
    void deleteById(ID id);
    List<T> findBy(Predicate<T> filter);
    int count();
}
