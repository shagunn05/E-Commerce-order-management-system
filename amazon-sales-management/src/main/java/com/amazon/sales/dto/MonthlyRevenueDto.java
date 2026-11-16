package com.amazon.sales.dto;

import java.math.BigDecimal;

/**
 * Monthly revenue and order performance trend.
 */
public class MonthlyRevenueDto {

    private String monthYear;
    private BigDecimal revenue;
    private Long orderCount;
    private Long unitsSold;

    public MonthlyRevenueDto() {}

    public MonthlyRevenueDto(String monthYear, BigDecimal revenue, Long orderCount, Long unitsSold) {
        this.monthYear = monthYear;
        this.revenue = revenue;
        this.orderCount = orderCount;
        this.unitsSold = unitsSold;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(Long unitsSold) {
        this.unitsSold = unitsSold;
    }
}
