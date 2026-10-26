package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics service demonstrating advanced Java Collections & Streams API features:
 * - Collectors.groupingBy()
 * - Collectors.summarizingDouble()
 * - Collectors.partitioningBy()
 * - Map operations and Stream reductions
 */
@Service
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public AnalyticsService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Groups orders by their OrderStatus and computes the count for each status.
     */
    public Map<OrderStatus, Long> getOrderCountsByStatus() {
        return orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
    }

    /**
     * Calculates total revenue generated per order status.
     */
    public Map<OrderStatus, Double> getRevenueByStatus() {
        return orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.summingDouble(Order::getTotalAmount)
                ));
    }

    /**
     * Computes summary statistics (count, sum, min, average, max) for all orders.
     */
    public DoubleSummaryStatistics getOrderValuationStatistics() {
        return orderRepository.findAll().stream()
                .collect(Collectors.summarizingDouble(Order::getTotalAmount));
    }

    /**
     * Partitions product inventory into Low-Stock (<= threshold) and Healthy-Stock (> threshold).
     */
    public Map<Boolean, List<Product>> partitionProductsByStock(int threshold) {
        return productRepository.findAll().stream()
                .collect(Collectors.partitioningBy(p -> p.getStockQuantity() <= threshold));
    }

    /**
     * Groups product catalog valuation (price * quantity) by product category.
     */
    public Map<String, Double> getInventoryValuationByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.summingDouble(p -> p.getPrice() * p.getStockQuantity())
                ));
    }

    /**
     * Finds the top N customers sorted by total spend across delivered/processing orders.
     */
    public Map<String, Double> getTopCustomersBySpending(int topN) {
        List<Order> validOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .filter(o -> o.getCustomer() != null)
                .toList();

        Map<String, Double> spendingByCustomer = validOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getCustomer().getName(),
                        Collectors.summingDouble(Order::getTotalAmount)
                ));

        // Sort descending by value and limit to topN using LinkedHashMap
        return spendingByCustomer.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
