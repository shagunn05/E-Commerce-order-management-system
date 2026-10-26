package com.ecommerce.service;

import com.ecommerce.model.DigitalProduct;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.exception.DuplicateResourceException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.PhysicalProduct;
import com.ecommerce.model.Product;
import com.ecommerce.model.SubscriptionProduct;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service managing Product catalog and stock inventory.
 * Demonstrates:
 * - Spring Data JPA integration: JpaRepository operations with Hibernate ORM
 * - Modern Java switch expressions for polymorphic entity creation
 * - Java Streams API for grouping, filtering, and summary metrics
 * - Declarative transactions with @Transactional
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(ProductRequestDto dto) {
        if (productRepository.findBySku(dto.getSku()).isPresent()) {
            throw new DuplicateResourceException("Product", "SKU", dto.getSku());
        }

        Product product = switch (dto.getProductType().toUpperCase()) {
            case "PHYSICAL" -> new PhysicalProduct(
                    null,
                    dto.getSku(),
                    dto.getName(),
                    dto.getCategory(),
                    dto.getPrice(),
                    dto.getStockQuantity(),
                    dto.getWeightKg() != null ? dto.getWeightKg() : 1.0
            );
            case "DIGITAL" -> new DigitalProduct(
                    null,
                    dto.getSku(),
                    dto.getName(),
                    dto.getCategory(),
                    dto.getPrice(),
                    dto.getStockQuantity(),
                    dto.getDownloadUrl() != null ? dto.getDownloadUrl() : "https://downloads.ecommerce.com/" + dto.getSku()
            );
            case "SUBSCRIPTION" -> new SubscriptionProduct(
                    null,
                    dto.getSku(),
                    dto.getName(),
                    dto.getCategory(),
                    dto.getPrice(),
                    dto.getStockQuantity(),
                    dto.getBillingPeriodMonths() != null ? dto.getBillingPeriodMonths() : 1
            );
            default -> throw new IllegalArgumentException("Unsupported product type: " + dto.getProductType());
        };

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional(readOnly = true)
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product with SKU '" + sku + "' not found."));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    @Transactional(readOnly = true)
    public Set<String> getDistinctCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        return productRepository.findAll().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    @Transactional
    public boolean updateStock(Long id, int newStock) {
        Product product = getProductById(id);
        product.setStockQuantity(newStock);
        productRepository.save(product);
        return true;
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public Map<String, List<Product>> getCatalogGroupedByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(Product::getCategory));
    }
}
