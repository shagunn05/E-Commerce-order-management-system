package com.amazon.sales.dto;

import java.math.BigDecimal;

/**
 * Top-selling product SKU analysis.
 */
public class TopProductDto {

    private String sku;
    private String style;
    private String category;
    private Long unitsSold;
    private BigDecimal revenue;
    private BigDecimal averagePrice;

    public TopProductDto() {}

    public TopProductDto(String sku, String style, String category, Long unitsSold, BigDecimal revenue, BigDecimal averagePrice) {
        this.sku = sku;
        this.style = style;
        this.category = category;
        this.unitsSold = unitsSold;
        this.revenue = revenue;
        this.averagePrice = averagePrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(Long unitsSold) {
        this.unitsSold = unitsSold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }
}
