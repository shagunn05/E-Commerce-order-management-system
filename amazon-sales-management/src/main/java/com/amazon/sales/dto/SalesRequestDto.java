package com.amazon.sales.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for creating or updating an Amazon sale record.
 */
public class SalesRequestDto {

    @NotBlank(message = "Order ID is mandatory")
    @Size(max = 50, message = "Order ID cannot exceed 50 characters")
    private String orderId;

    @NotNull(message = "Date is mandatory")
    private LocalDate date;

    @NotBlank(message = "Status is mandatory")
    @Size(max = 50, message = "Status cannot exceed 50 characters")
    private String status;

    private String fulfilment;
    private String salesChannel;
    private String shipServiceLevel;
    private String style;

    @NotBlank(message = "SKU is mandatory")
    @Size(max = 100, message = "SKU cannot exceed 100 characters")
    private String sku;

    @NotBlank(message = "Category is mandatory")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    private String size;
    private String asin;
    private String courierStatus;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer qty;

    private String currency;

    @DecimalMin(value = "0.00", message = "Amount cannot be negative")
    private BigDecimal amount;

    private String shipCity;
    private String shipState;
    private String shipPostalCode;
    private String shipCountry;
    private String promotionIds;
    private Boolean b2b;
    private String fulfilledBy;

    public SalesRequestDto() {
        this.qty = 1;
        this.currency = "INR";
        this.amount = BigDecimal.ZERO;
        this.b2b = false;
        this.shipCountry = "IN";
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getCourierStatus() {
        return courierStatus;
    }

    public void setCourierStatus(String courierStatus) {
        this.courierStatus = courierStatus;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getShipPostalCode() {
        return shipPostalCode;
    }

    public void setShipPostalCode(String shipPostalCode) {
        this.shipPostalCode = shipPostalCode;
    }

    public String getShipCountry() {
        return shipCountry;
    }

    public void setShipCountry(String shipCountry) {
        this.shipCountry = shipCountry;
    }

    public String getPromotionIds() {
        return promotionIds;
    }

    public void setPromotionIds(String promotionIds) {
        this.promotionIds = promotionIds;
    }

    public Boolean getB2b() {
        return b2b;
    }

    public void setB2b(Boolean b2b) {
        this.b2b = b2b;
    }

    public String getFulfilledBy() {
        return fulfilledBy;
    }

    public void setFulfilledBy(String fulfilledBy) {
        this.fulfilledBy = fulfilledBy;
    }
}
