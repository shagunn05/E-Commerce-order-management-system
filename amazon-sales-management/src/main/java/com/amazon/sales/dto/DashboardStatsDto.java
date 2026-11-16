package com.amazon.sales.dto;

import java.math.BigDecimal;

/**
 * High-level aggregated KPIs for dashboard metric cards.
 */
public class DashboardStatsDto {

    private Long totalRecords;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long totalUnits;
    private BigDecimal averageOrderValue;
    private Double deliveredRate;
    private Double cancelledRate;
    private Double b2bPercentage;

    public DashboardStatsDto() {
        this.totalRecords = 0L;
        this.totalOrders = 0L;
        this.totalRevenue = BigDecimal.ZERO;
        this.totalUnits = 0L;
        this.averageOrderValue = BigDecimal.ZERO;
        this.deliveredRate = 0.0;
        this.cancelledRate = 0.0;
        this.b2bPercentage = 0.0;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Long totalUnits) {
        this.totalUnits = totalUnits;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public Double getDeliveredRate() {
        return deliveredRate;
    }

    public void setDeliveredRate(Double deliveredRate) {
        this.deliveredRate = deliveredRate;
    }

    public Double getCancelledRate() {
        return cancelledRate;
    }

    public void setCancelledRate(Double cancelledRate) {
        this.cancelledRate = cancelledRate;
    }

    public Double getB2bPercentage() {
        return b2bPercentage;
    }

    public void setB2bPercentage(Double b2bPercentage) {
        this.b2bPercentage = b2bPercentage;
    }
}
