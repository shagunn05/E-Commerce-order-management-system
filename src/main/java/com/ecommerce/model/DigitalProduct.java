package com.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Concrete Digital Product Entity.
 * Inherits from Product table via JOINED inheritance.
 */
@Entity
@Table(name = "digital_products")
@DiscriminatorValue("DIGITAL")
public class DigitalProduct extends Product {

    @Column(name = "download_url", nullable = false, length = 500)
    private String downloadUrl;

    public DigitalProduct() {
        super();
    }

    public DigitalProduct(Long id, String sku, String name, String category, double price, int stockQuantity, String downloadUrl) {
        super(id, sku, name, category, price, stockQuantity);
        this.downloadUrl = (downloadUrl != null) ? downloadUrl : "https://downloads.ecommerce.com/" + sku;
    }

    @Override
    public String getProductType() {
        return "DIGITAL";
    }

    @Override
    public double calculateShippingCost() {
        // Digital delivery has zero shipping cost
        return 0.0;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
