package com.ecommerce.dto;

import java.util.List;

public class OrderRequestDto {
    private Long customerId;
    private List<OrderItemDto> items;

    // Getters aur Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}