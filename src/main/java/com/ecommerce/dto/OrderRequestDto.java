package com.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Data Transfer Object for creating a new Order.
 */
public class OrderRequestDto {

    @NotNull(message = "Customer ID is required.")
    private Long customerId;

    @NotEmpty(message = "Order must contain at least one line item.")
    @Valid
    private List<OrderItemDto> items;

    public OrderRequestDto() {
    }

    public OrderRequestDto(Long customerId, List<OrderItemDto> items) {
        this.customerId = customerId;
        this.items = items;
    }

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
