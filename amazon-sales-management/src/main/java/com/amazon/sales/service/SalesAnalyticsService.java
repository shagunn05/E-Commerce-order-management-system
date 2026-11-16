package com.amazon.sales.service;

import com.amazon.sales.dto.*;
import com.amazon.sales.repository.SalesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service providing aggregation, business intelligence, and real-time sales metrics.
 */
@Service
@Transactional(readOnly = true)
public class SalesAnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(SalesAnalyticsService.class);

    private final SalesRepository salesRepository;

    public SalesAnalyticsService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        log.info("Computing dashboard summary KPIs");
        return salesRepository.getDashboardStats();
    }

    public BigDecimal getTotalRevenue() {
        return salesRepository.getTotalRevenue();
    }

    public Long getTotalOrders() {
        return salesRepository.getTotalOrders();
    }

    public Long getTotalUnits() {
        return salesRepository.getTotalUnits();
    }

    public BigDecimal getAverageOrderValue() {
        return salesRepository.getAverageOrderValue();
    }

    public List<DailySalesDto> getSalesOverview() {
        return salesRepository.getSalesOverview();
    }

    public List<CategorySalesDto> getCategorySales() {
        log.info("Fetching category-wise sales analytics");
        return salesRepository.getCategorySales();
    }

    public List<TopProductDto> getTopProducts(int limit) {
        log.info("Fetching top {} selling products", limit);
        return salesRepository.getTopProducts(limit);
    }

    public List<LocationSalesDto> getSalesByState(int limit) {
        log.info("Fetching state-wise sales distribution (top {})", limit);
        return salesRepository.getSalesByState(limit);
    }

    public List<LocationSalesDto> getSalesByCity(int limit) {
        log.info("Fetching city-wise sales distribution (top {})", limit);
        return salesRepository.getSalesByCity(limit);
    }

    public List<StatusDistributionDto> getSalesBySize() {
        return salesRepository.getSalesBySize();
    }

    public List<StatusDistributionDto> getSalesByChannel() {
        return salesRepository.getSalesByChannel();
    }

    public List<StatusDistributionDto> getFulfilmentAnalysis() {
        return salesRepository.getFulfilmentAnalysis();
    }

    public List<StatusDistributionDto> getOrderStatusDistribution() {
        return salesRepository.getOrderStatusDistribution();
    }

    public List<StatusDistributionDto> getCourierStatusDistribution() {
        return salesRepository.getCourierStatusDistribution();
    }

    public List<MonthlyRevenueDto> getMonthlyRevenue() {
        return salesRepository.getMonthlyRevenue();
    }

    public List<DailySalesDto> getDailySales(int limit) {
        return salesRepository.getDailySales(limit);
    }

    public SalesAnalyticsDto getFullAnalyticsOverview() {
        log.info("Assembling comprehensive analytics overview");
        DashboardStatsDto stats = getDashboardStats();
        List<CategorySalesDto> topCategories = getCategorySales();
        List<LocationSalesDto> topStates = getSalesByState(10);
        List<LocationSalesDto> topCities = getSalesByCity(10);
        List<MonthlyRevenueDto> monthlyTrend = getMonthlyRevenue();

        return new SalesAnalyticsDto(stats, topCategories, topStates, topCities, monthlyTrend);
    }
}
