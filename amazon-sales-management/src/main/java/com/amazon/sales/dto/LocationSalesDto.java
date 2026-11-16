package com.amazon.sales.dto;

import java.math.BigDecimal;

/**
 * Geographical sales aggregation by state or city.
 */
public class LocationSalesDto {

    private String location;
    private Long totalOrders;
    private Long totalUnits;
    private BigDecimal totalRevenue;
    private Double percentage;

    public LocationSalesDto() {}

    public LocationSalesDto(String location, Long totalOrders, Long totalUnits,
                            BigDecimal totalRevenue, Double percentage) {
        this.location = location;
        this.totalOrders = totalOrders;
        this.totalUnits = totalUnits;
        this.totalRevenue = totalRevenue;
        this.percentage = percentage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
