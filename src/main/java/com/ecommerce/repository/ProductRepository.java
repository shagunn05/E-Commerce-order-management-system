package com.ecommerce.repository;

import com.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByStockQuantityLessThan(int quantity);
    boolean existsById(Long id);
    boolean updateStock(Long id, int newStock);
    boolean deleteById(Long id);
}
