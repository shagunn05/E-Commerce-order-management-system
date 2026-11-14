package com.managementsystem.model;

/**
 * Concrete class representing physical electronic inventory assets.
 */
public class ElectronicsItem extends Item {
    private String brand;
    private String modelName;
    private int warrantyMonths;
    private String serialPrefix;

    public ElectronicsItem(String id, String title, String brand, String modelName,
                           double unitPrice, int quantity, int reorderThreshold, int warrantyMonths, String serialPrefix) {
        super(id, title, "Electronics", unitPrice, quantity, reorderThreshold);
        this.brand = brand != null ? brand.trim() : "Generic";
        this.modelName = modelName != null ? modelName.trim() : "Standard";
        this.warrantyMonths = Math.max(warrantyMonths, 0);
        this.serialPrefix = serialPrefix != null ? serialPrefix.trim() : "SN-ELEC";
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = Math.max(warrantyMonths, 0);
    }

    public String getSerialPrefix() {
        return serialPrefix;
    }

    @Override
    public String getItemType() {
        return "ELECTRONIC";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("Brand: %s | Model: %s | Warranty: %d months | Prefix: %s",
                brand, modelName, warrantyMonths, serialPrefix);
    }

    @Override
    public boolean matches(String query) {
        if (super.matches(query)) return true;
        if (query == null) return false;
        String q = query.toLowerCase();
        return brand.toLowerCase().contains(q) ||
               modelName.toLowerCase().contains(q) ||
               serialPrefix.toLowerCase().contains(q);
    }
}
