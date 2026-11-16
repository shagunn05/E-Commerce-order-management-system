package com.amazon.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Daily sales trend metric.
 */
public class DailySalesDto {

    private LocalDate date;
    private BigDecimal revenue;
    private Long orderCount;
    private Long unitsSold;

    public DailySalesDto() {}

    public DailySalesDto(LocalDate date, BigDecimal revenue, Long orderCount, Long unitsSold) {
        this.date = date;
        this.revenue = revenue;
        this.orderCount = orderCount;
        this.unitsSold = unitsSold;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
