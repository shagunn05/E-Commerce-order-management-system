package com.amazon.sales.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Comprehensive business intelligence analytics overview.
 */
public class SalesAnalyticsDto {

    private DashboardStatsDto stats;
    private List<CategorySalesDto> topCategories;
    private List<LocationSalesDto> topStates;
    private List<LocationSalesDto> topCities;
    private List<MonthlyRevenueDto> monthlyTrend;

    public SalesAnalyticsDto() {}

    public SalesAnalyticsDto(DashboardStatsDto stats,
                             List<CategorySalesDto> topCategories,
                             List<LocationSalesDto> topStates,
                             List<LocationSalesDto> topCities,
                             List<MonthlyRevenueDto> monthlyTrend) {
        this.stats = stats;
        this.topCategories = topCategories;
        this.topStates = topStates;
        this.topCities = topCities;
        this.monthlyTrend = monthlyTrend;
    }

    public DashboardStatsDto getStats() {
        return stats;
    }

    public void setStats(DashboardStatsDto stats) {
        this.stats = stats;
    }

    public List<CategorySalesDto> getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(List<CategorySalesDto> topCategories) {
        this.topCategories = topCategories;
    }

    public List<LocationSalesDto> getTopStates() {
        return topStates;
    }

    public void setTopStates(List<LocationSalesDto> topStates) {
        this.topStates = topStates;
    }

    public List<LocationSalesDto> getTopCities() {
        return topCities;
    }

    public void setTopCities(List<LocationSalesDto> topCities) {
        this.topCities = topCities;
    }

    public List<MonthlyRevenueDto> getMonthlyTrend() {
        return monthlyTrend;
    }

    public void setMonthlyTrend(List<MonthlyRevenueDto> monthlyTrend) {
        this.monthlyTrend = monthlyTrend;
    }
}
