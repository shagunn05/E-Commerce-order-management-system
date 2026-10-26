package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository("inMemoryProductRepository")
public class InMemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(100);

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.incrementAndGet());
        }
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> findByCategoryIgnoreCase(String category) {
        return store.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByStockQuantityLessThan(int quantity) {
        return store.values().stream()
                .filter(p -> p.getStockQuantity() < quantity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return store.values().stream()
                .filter(p -> p.getSku().equalsIgnoreCase(sku))
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return store.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateStock(Long id, int newStock) {
        Product p = store.get(id);
        if (p != null) {
            p.setStockQuantity(newStock);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}