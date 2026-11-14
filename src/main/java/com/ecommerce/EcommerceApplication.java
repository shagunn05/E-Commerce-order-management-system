package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot Main Application Entry Point.
 * Demonstrates:
 * - @SpringBootApplication: Component scanning, auto-configuration, and property binding
 * - @EnableTransactionManagement: Declarative ACID transaction boundaries
 */
@SpringBootApplication
@EnableTransactionManagement
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
