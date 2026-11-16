package com.amazon.sales.controller;

import com.amazon.sales.dto.DailySalesDto;
import com.amazon.sales.dto.DashboardStatsDto;
import com.amazon.sales.service.SalesAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for high-level dashboard metrics and KPI calculations.
 * Base URL: /api/v1/dashboard
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final SalesAnalyticsService analyticsService;

    public DashboardController(SalesAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardStatsDto> getSummary() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<Map<String, Object>> getTotalRevenue() {
        BigDecimal revenue = analyticsService.getTotalRevenue();
        Map<String, Object> res = new HashMap<>();
        res.put("totalRevenue", revenue);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/total-orders")
    public ResponseEntity<Map<String, Object>> getTotalOrders() {
        Long orders = analyticsService.getTotalOrders();
        Map<String, Object> res = new HashMap<>();
        res.put("totalOrders", orders);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/total-units")
    public ResponseEntity<Map<String, Object>> getTotalUnits() {
        Long units = analyticsService.getTotalUnits();
        Map<String, Object> res = new HashMap<>();
        res.put("totalUnits", units);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/average-order-value")
    public ResponseEntity<Map<String, Object>> getAverageOrderValue() {
        BigDecimal aov = analyticsService.getAverageOrderValue();
        Map<String, Object> res = new HashMap<>();
        res.put("averageOrderValue", aov);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/sales-overview")
    public ResponseEntity<List<DailySalesDto>> getSalesOverview() {
        return ResponseEntity.ok(analyticsService.getSalesOverview());
    }
}
