package com.amazon.sales.dto;

import java.math.BigDecimal;

/**
 * Sales aggregation grouped by product category.
 */
public class CategorySalesDto {

    private String category;
    private Long totalOrders;
    private Long totalUnits;
    private BigDecimal totalRevenue;
    private BigDecimal averagePrice;
    private Double percentage;

    public CategorySalesDto() {}

    public CategorySalesDto(String category, Long totalOrders, Long totalUnits,
                            BigDecimal totalRevenue, BigDecimal averagePrice, Double percentage) {
        this.category = category;
        this.totalOrders = totalOrders;
        this.totalUnits = totalUnits;
        this.totalRevenue = totalRevenue;
        this.averagePrice = averagePrice;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Long totalUnits) {
        this.totalUnits = totalUnits;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
