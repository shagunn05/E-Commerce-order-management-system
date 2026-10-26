package com.ecommerce.web.controller;

import com.ecommerce.dto.OrderRequestDto;
import com.ecommerce.dto.OrderResponseDto;
import com.ecommerce.model.OrderFulfillmentRequest;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Spring Boot REST Controller for Order Management and Fulfillment.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto request) {
        OrderResponseDto response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    /**
     * Polls the highest-priority fulfillment job from the PriorityQueue and dispatches it.
     */
    @PostMapping("/dispatch-next")
    public ResponseEntity<OrderResponseDto> dispatchNextPriorityOrder() {
        OrderResponseDto dispatched = orderService.dispatchNextPriorityOrder();
        return ResponseEntity.ok(dispatched);
    }

    /**
     * Inspects current priority queue status.
     */
    @GetMapping("/fulfillment-queue")
    public ResponseEntity<List<OrderFulfillmentRequest>> getFulfillmentQueue() {
        return ResponseEntity.ok(orderService.getFulfillmentQueueStatus());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<OrderResponseDto> shipOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.markOrderShipped(id));
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<OrderResponseDto> deliverOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.markOrderDelivered(id));
    }
}
