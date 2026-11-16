package com.amazon.sales.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain entity mapping table "public.amazon_sales".
 */
public class AmazonSale {

    private Long indexId;
    private String orderId;
    private LocalDate date;
    private String status;
    private String fulfilment;
    private String salesChannel;
    private String shipServiceLevel;
    private String style;
    private String sku;
    private String category;
    private String size;
    private String asin;
    private String courierStatus;
    private Integer qty;
    private String currency;
    private BigDecimal amount;
    private String shipCity;
    private String shipState;
    private String shipPostalCode;
    private String shipCountry;
    private String promotionIds;
    private Boolean b2b;
    private String fulfilledBy;

    public AmazonSale() {
        this.qty = 1;
        this.currency = "INR";
        this.amount = BigDecimal.ZERO;
        this.b2b = false;
        this.shipCountry = "IN";
    }

    public AmazonSale(Long indexId, String orderId, LocalDate date, String status, String fulfilment,
                      String salesChannel, String shipServiceLevel, String style, String sku,
                      String category, String size, String asin, String courierStatus,
                      Integer qty, String currency, BigDecimal amount, String shipCity,
                      String shipState, String shipPostalCode, String shipCountry,
                      String promotionIds, Boolean b2b, String fulfilledBy) {
        this.indexId = indexId;
        this.orderId = orderId;
        this.date = date;
        this.status = status;
        this.fulfilment = fulfilment;
        this.salesChannel = salesChannel;
        this.shipServiceLevel = shipServiceLevel;
        this.style = style;
        this.sku = sku;
        this.category = category;
        this.size = size;
        this.asin = asin;
        this.courierStatus = courierStatus;
        this.qty = (qty != null) ? qty : 1;
        this.currency = (currency != null) ? currency : "INR";
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
        this.shipCity = shipCity;
        this.shipState = shipState;
        this.shipPostalCode = shipPostalCode;
        this.shipCountry = (shipCountry != null) ? shipCountry : "IN";
        this.promotionIds = promotionIds;
        this.b2b = (b2b != null) ? b2b : false;
        this.fulfilledBy = fulfilledBy;
    }

    public Long getIndexId() {
        return indexId;
    }

    public void setIndexId(Long indexId) {
        this.indexId = indexId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AmazonSale that)) return false;
        return Objects.equals(indexId, that.indexId) ||
                (Objects.equals(orderId, that.orderId) && Objects.equals(sku, that.sku));
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexId, orderId, sku);
    }

    @Override
    public String toString() {
        return "AmazonSale{" +
                "indexId=" + indexId +
                ", orderId='" + orderId + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", sku='" + sku + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", qty=" + qty +
                '}';
    }
}
