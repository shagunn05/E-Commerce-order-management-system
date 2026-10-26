package com.ecommerce.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @Async("orderEventExecutor")
    @EventListener
    public void handleOrderCreatedNotification(OrderCreatedEvent event) {
        log.info("[ASYNC-EVENT] Thread [{}] Received OrderCreatedEvent for Order Number: {}",
                Thread.currentThread().getName(), event.getOrderNumber());

        sendCustomerConfirmationEmail(event);
        notifyWarehouseFulfillment(event);
    }

    private void sendCustomerConfirmationEmail(OrderCreatedEvent event) {
        log.info("[NOTIFICATION] Sent order confirmation email to [{}] for Order #{} (Total: ${})",
                event.getCustomerEmail(), event.getOrderNumber(), event.getTotalAmount());
    }

    private void notifyWarehouseFulfillment(OrderCreatedEvent event) {
        log.info("[WAREHOUSE] Automated dispatch slip generated for Order #{}. Line items: {}",
                event.getOrderNumber(), event.getItemsCount());
    }
}
