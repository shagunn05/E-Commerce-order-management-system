package com.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductRequestDto {

    @NotBlank(message = "SKU is required.")
    private String sku;

    @NotBlank(message = "Product name is required.")
    private String name;

    @NotBlank(message = "Category is required.")
    private String category;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero.")
    private Double price;

    @NotNull(message = "Stock quantity is required.")
    @Min(value = 0, message = "Stock cannot be negative.")
    private Integer stockQuantity;

    @NotBlank(message = "Product type is required (PHYSICAL, DIGITAL, or SUBSCRIPTION).")
    private String productType;

    private Double weightKg;
    private String downloadUrl;
    private Integer billingPeriodMonths;

    public ProductRequestDto() {}

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public Integer getBillingPeriodMonths() { return billingPeriodMonths; }
    public void setBillingPeriodMonths(Integer billingPeriodMonths) { this.billingPeriodMonths = billingPeriodMonths; }
}
