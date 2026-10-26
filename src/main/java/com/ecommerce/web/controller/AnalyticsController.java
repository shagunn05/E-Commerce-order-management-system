package com.ecommerce.web.controller;

import com.ecommerce.model.OrderStatus;
import com.ecommerce.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.DoubleSummaryStatistics;
import java.util.Map;

/**
 * Spring Boot REST Controller exposing business analytics backed by Java Streams API.
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/order-status-counts")
    public ResponseEntity<Map<OrderStatus, Long>> getOrderCountsByStatus() {
        return ResponseEntity.ok(analyticsService.getOrderCountsByStatus());
    }

    @GetMapping("/revenue-by-status")
    public ResponseEntity<Map<OrderStatus, Double>> getRevenueByStatus() {
        return ResponseEntity.ok(analyticsService.getRevenueByStatus());
    }

    @GetMapping("/valuation-stats")
    public ResponseEntity<DoubleSummaryStatistics> getValuationStatistics() {
        return ResponseEntity.ok(analyticsService.getOrderValuationStatistics());
    }

    @GetMapping("/inventory-valuation")
    public ResponseEntity<Map<String, Double>> getInventoryValuation() {
        return ResponseEntity.ok(analyticsService.getInventoryValuationByCategory());
    }

    @GetMapping("/top-customers")
    public ResponseEntity<Map<String, Double>> getTopCustomers(@RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(analyticsService.getTopCustomersBySpending(topN));
    }
}
