package com.managementsystem.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Thread-safe In-Memory Generic Repository implementation using ConcurrentHashMap and Stream API.
 */
public class InMemoryRepository<T, ID> implements Repository<T, ID> {
    private final Map<ID, T> storage;
    private final Function<T, ID> idExtractor;

    public InMemoryRepository(Function<T, ID> idExtractor) {
        this.storage = new ConcurrentHashMap<>();
        this.idExtractor = idExtractor;
    }

    @Override
    public void save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        ID id = idExtractor.apply(entity);
        if (id == null) {
            throw new IllegalArgumentException("Entity ID cannot be null");
        }
        storage.put(id, entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) return false;
        return storage.containsKey(id);
    }

    @Override
    public void deleteById(ID id) {
        if (id != null) {
            storage.remove(id);
        }
    }

    @Override
    public List<T> findBy(Predicate<T> filter) {
        return storage.values().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public int count() {
        return storage.size();
    }
}
