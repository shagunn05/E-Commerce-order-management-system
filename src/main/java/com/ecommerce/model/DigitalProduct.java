package com.ecommerce.model;

public class DigitalProduct extends Product {

    private String downloadUrl;

    public DigitalProduct() {
        super();
    }

    public DigitalProduct(Long id, String sku, String name, String category, double price, int stockQuantity, String downloadUrl) {
        super(id, sku, name, category, price, stockQuantity);
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String getProductType() {
        return "DIGITAL";
    }

    @Override
    public double calculateShippingCost() {

        return 0.0;
    }
}