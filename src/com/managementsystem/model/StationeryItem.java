package com.managementsystem.model;

/**
 * Concrete class representing consumables and stationery inventory items.
 */
public class StationeryItem extends Item {
    private String unitType; // e.g., "Box", "Pack of 10", "Single Piece"
    private String material;

    public StationeryItem(String id, String title, String unitType, String material,
                          double unitPrice, int quantity, int reorderThreshold) {
        super(id, title, "Stationery", unitPrice, quantity, reorderThreshold);
        this.unitType = unitType != null ? unitType.trim() : "Units";
        this.material = material != null ? material.trim() : "Standard";
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    @Override
    public String getItemType() {
        return "STATIONERY";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("Unit: %s | Material: %s", unitType, material);
    }
}
