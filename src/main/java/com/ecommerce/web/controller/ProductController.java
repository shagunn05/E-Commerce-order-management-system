package com.ecommerce.web.controller;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Spring Boot REST Controller for Product Catalog.
 * Demonstrates:
 * - @RestController and @RequestMapping
 * - HTTP Verbs: @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
 * - Path variables, Request parameters, Request body with @Valid
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequestDto request) {
        Product created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getDistinctCategories() {
        return ResponseEntity.ok(productService.getDistinctCategories());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterByPrice(
            @RequestParam(defaultValue = "0.0") double minPrice,
            @RequestParam(defaultValue = "100000.0") double maxPrice) {
        return ResponseEntity.ok(productService.filterByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable Long id,
            @RequestParam int newStock) {
        boolean updated = productService.updateStock(id, newStock);
        return ResponseEntity.ok(Map.of("productId", id, "newStock", newStock, "updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("productId", id, "deleted", deleted));
    }
}
