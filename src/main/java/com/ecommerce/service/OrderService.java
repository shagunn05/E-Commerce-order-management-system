package com.ecommerce.service;

import com.ecommerce.domain.event.OrderCreatedEvent;
import com.ecommerce.dto.OrderItemDto;
import com.ecommerce.dto.OrderRequestDto;
import com.ecommerce.dto.OrderResponseDto;
import com.ecommerce.exception.EcommerceException;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.*;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service managing Order lifecycle, fulfillment scheduling, and event publishing.
 * Demonstrates:
 * - Spring Core: @Service, Constructor Injection, @Value property injection, @PostConstruct & @PreDestroy
 * - Domain Events: ApplicationEventPublisher broadcasting OrderCreatedEvent asynchronously
 * - Collections Framework: PriorityQueue<OrderFulfillmentRequest> for priority-based dispatch
 * - Exception Handling: Robust validation and custom domain exceptions
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Collections Framework: PriorityQueue for order fulfillment dispatching
    private final PriorityQueue<OrderFulfillmentRequest> fulfillmentQueue = new PriorityQueue<>();

    // Thread-safe order number counter
    private final AtomicLong orderCounter = new AtomicLong(1000);

    @Value("${ecommerce.app.store-name:Apex E-Commerce}")
    private String storeName;

    @Value("${ecommerce.app.max-order-items:50}")
    private int maxOrderItems;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CustomerRepository customerRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        log.info("Initialized OrderService for store: [{}]. Loading pending orders into PriorityQueue...", storeName);
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        for (Order o : pendingOrders) {
            fulfillmentQueue.offer(new OrderFulfillmentRequest(
                    o.getId(),
                    o.getOrderNumber(),
                    o.getPriorityLevel(),
                    o.getTotalAmount()
            ));
        }
        log.info("Loaded {} pending order(s) into fulfillment queue.", fulfillmentQueue.size());
    }

    @PreDestroy
    public void cleanup() {
        log.info("Shutting down OrderService. Pending fulfillment jobs in queue: {}", fulfillmentQueue.size());
    }

    /**
     * Places a new order:
     * 1. Validates customer and products
     * 2. Checks and reserves stock
     * 3. Calculates totals and discounts via OOP Polymorphism
     * 4. Persists via JDBC transaction
     * 5. Enqueues in PriorityQueue
     * 6. Awards customer loyalty points
     * 7. Publishes asynchronous OrderCreatedEvent
     */
    public synchronized OrderResponseDto placeOrder(OrderRequestDto request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        if (request.getItems().size() > maxOrderItems) {
            throw new EcommerceException("Order exceeds maximum allowable line items of " + maxOrderItems);
        }

        // Generate human-readable order number
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String orderNumber = String.format("ORD-%s-%04d", timestamp, orderCounter.incrementAndGet());

        Order order = new Order(null, orderNumber, customer);

        // Map items and validate inventory
        for (OrderItemDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemDto.getProductId()));

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new InsufficientStockException(product.getSku(), itemDto.getQuantity(), product.getStockQuantity());
            }

            // Deduct stock defensively
            product.deductStock(itemDto.getQuantity());
            productRepository.updateStock(product.getId(), product.getStockQuantity());

            OrderItem orderItem = new OrderItem(product, itemDto.getQuantity());
            order.addItem(orderItem);
        }

        // Recalculate totals (includes shipping and customer tier discounts)
        order.recalculateTotals();

        // Save order and order_items via JDBC
        Order savedOrder = orderRepository.save(order);

        // Offer into Collections PriorityQueue
        OrderFulfillmentRequest fulfillmentJob = new OrderFulfillmentRequest(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getPriorityLevel(),
                savedOrder.getTotalAmount()
        );
        fulfillmentQueue.offer(fulfillmentJob);

        // Award loyalty points: 1 point per $10 spent
        int earnedPoints = (int) (savedOrder.getTotalAmount() / 10.0);
        customer.addLoyaltyPoints(earnedPoints);
        customerRepository.updateLoyaltyPoints(customer.getId(), customer.getLoyaltyPoints(), customer.getTier().name());

        // Publish Domain Event for asynchronous processing
        eventPublisher.publishEvent(new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                customer.getId(),
                customer.getEmail(),
                customer.getName(),
                savedOrder.getTotalAmount(),
                savedOrder.getItems().size()
        ));

        return mapToResponseDto(savedOrder);
    }

    public OrderResponseDto getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order with number", orderNumber));
        return mapToResponseDto(order);
    }

    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return mapToResponseDto(order);
    }

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDto> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Polls the highest-priority order from the PriorityQueue and marks it as PROCESSING.
     */
    public synchronized OrderResponseDto dispatchNextPriorityOrder() {
        if (fulfillmentQueue.isEmpty()) {
            throw new EcommerceException("No pending orders available in fulfillment queue.");
        }

        OrderFulfillmentRequest job = fulfillmentQueue.poll();
        Order order = orderRepository.findById(job.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", job.getOrderId()));

        order.markProcessing();
        orderRepository.updateStatus(order.getId(), OrderStatus.PROCESSING);

        log.info("Dispatched priority fulfillment for Order [{}] (Customer Priority: {})",
                order.getOrderNumber(), job.getPriority());

        return mapToResponseDto(order);
    }

    public OrderResponseDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        order.cancel();
        orderRepository.updateStatus(order.getId(), OrderStatus.CANCELLED);

        // Restock inventory for cancelled items
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                product.restock(item.getQuantity());
                productRepository.updateStock(product.getId(), product.getStockQuantity());
            }
        }

        // Remove from fulfillment queue if still pending
        fulfillmentQueue.removeIf(req -> req.getOrderId().equals(orderId));

        return mapToResponseDto(order);
    }

    public OrderResponseDto markOrderShipped(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        order.markShipped();
        orderRepository.updateStatus(order.getId(), OrderStatus.SHIPPED);
        return mapToResponseDto(order);
    }

    public OrderResponseDto markOrderDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        order.markDelivered();
        orderRepository.updateStatus(order.getId(), OrderStatus.DELIVERED);
        return mapToResponseDto(order);
    }

    public List<OrderFulfillmentRequest> getFulfillmentQueueStatus() {
        return new ArrayList<>(fulfillmentQueue);
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());

        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
            dto.setCustomerName(order.getCustomer().getName());
            dto.setCustomerTier(order.getCustomer().getTier().name());
        }

        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountApplied(order.getDiscountApplied());
        dto.setPriorityLevel(order.getPriorityLevel());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderResponseDto.OrderItemDetailDto> itemDtos = order.getItems().stream()
                .map(i -> new OrderResponseDto.OrderItemDetailDto(
                        i.getProduct() != null ? i.getProduct().getId() : null,
                        i.getProduct() != null ? i.getProduct().getName() : "Unknown",
                        i.getProduct() != null ? i.getProduct().getSku() : "N/A",
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getSubtotal()
                ))
                .collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}
