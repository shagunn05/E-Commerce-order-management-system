package com.amazon.sales.controller;

import com.amazon.sales.dto.*;
import com.amazon.sales.service.SalesAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for multi-dimensional business analytics.
 * Base URL: /api/v1/analytics
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final SalesAnalyticsService analyticsService;

    public AnalyticsController(SalesAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/category-sales")
    public ResponseEntity<List<CategorySalesDto>> getCategorySales() {
        return ResponseEntity.ok(analyticsService.getCategorySales());
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getTopProducts(limit));
    }

    @GetMapping("/sales-by-state")
    public ResponseEntity<List<LocationSalesDto>> getSalesByState(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getSalesByState(limit));
    }

    @GetMapping("/sales-by-city")
    public ResponseEntity<List<LocationSalesDto>> getSalesByCity(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getSalesByCity(limit));
    }

    @GetMapping("/sales-by-size")
    public ResponseEntity<List<StatusDistributionDto>> getSalesBySize() {
        return ResponseEntity.ok(analyticsService.getSalesBySize());
    }

    @GetMapping("/sales-by-channel")
    public ResponseEntity<List<StatusDistributionDto>> getSalesByChannel() {
        return ResponseEntity.ok(analyticsService.getSalesByChannel());
    }

    @GetMapping("/fulfilment-analysis")
    public ResponseEntity<List<StatusDistributionDto>> getFulfilmentAnalysis() {
        return ResponseEntity.ok(analyticsService.getFulfilmentAnalysis());
    }

    @GetMapping("/order-status")
    public ResponseEntity<List<StatusDistributionDto>> getOrderStatus() {
        return ResponseEntity.ok(analyticsService.getOrderStatusDistribution());
    }

    @GetMapping("/courier-status")
    public ResponseEntity<List<StatusDistributionDto>> getCourierStatus() {
        return ResponseEntity.ok(analyticsService.getCourierStatusDistribution());
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueDto>> getMonthlyRevenue() {
        return ResponseEntity.ok(analyticsService.getMonthlyRevenue());
    }

    @GetMapping("/daily-sales")
    public ResponseEntity<List<DailySalesDto>> getDailySales(
            @RequestParam(name = "limit", defaultValue = "30") int limit) {
        return ResponseEntity.ok(analyticsService.getDailySales(limit));
    }

    @GetMapping("/overview")
    public ResponseEntity<SalesAnalyticsDto> getFullAnalyticsOverview() {
        return ResponseEntity.ok(analyticsService.getFullAnalyticsOverview());
    }
}
