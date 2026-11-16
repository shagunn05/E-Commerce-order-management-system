package com.amazon.sales.dto;

import java.math.BigDecimal;

/**
 * Categorical breakdown representation for status, courier, size, fulfillment, etc.
 */
public class StatusDistributionDto {

    private String name;
    private Long count;
    private BigDecimal revenue;
    private Double percentage;

    public StatusDistributionDto() {}

    public StatusDistributionDto(String name, Long count, BigDecimal revenue, Double percentage) {
        this.name = name;
        this.count = count;
        this.revenue = revenue;
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
