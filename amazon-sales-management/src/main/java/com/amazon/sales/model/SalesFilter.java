package com.amazon.sales.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Encapsulates search and dynamic filter criteria.
 */
public class SalesFilter {

    private String keyword;
    private String category;
    private String status;
    private String fulfilment;
    private String salesChannel;
    private String shipServiceLevel;
    private String courierStatus;
    private Boolean b2b;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String shipCity;
    private String shipState;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;

    public SalesFilter() {
        this.page = 0;
        this.size = 20;
        this.sortBy = "index_id";
        this.sortDirection = "DESC";
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFulfilment() {
        return fulfilment;
    }

    public void setFulfilment(String fulfilment) {
        this.fulfilment = fulfilment;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }

    public String getShipServiceLevel() {
        return shipServiceLevel;
    }

    public void setShipServiceLevel(String shipServiceLevel) {
        this.shipServiceLevel = shipServiceLevel;
    }

    public String getCourierStatus() {
        return courierStatus;
    }

    public void setCourierStatus(String courierStatus) {
        this.courierStatus = courierStatus;
    }

    public Boolean getB2b() {
        return b2b;
    }

    public void setB2b(Boolean b2b) {
        this.b2b = b2b;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getShipCity() {
        return shipCity;
    }

    public void setShipCity(String shipCity) {
        this.shipCity = shipCity;
    }

    public String getShipState() {
        return shipState;
    }

    public void setShipState(String shipState) {
        this.shipState = shipState;
    }

    public Integer getPage() {
        return (page != null && page >= 0) ? page : 0;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return (size != null && size > 0 && size <= 200) ? size : 20;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return (sortBy != null && !sortBy.isBlank()) ? sortBy : "index_id";
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return (sortDirection != null && sortDirection.equalsIgnoreCase("ASC")) ? "ASC" : "DESC";
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
