package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${ecommerce.async.core-pool-size:4}")
    private int corePoolSize;

    @Value("${ecommerce.async.max-pool-size:8}")
    private int maxPoolSize;

    @Value("${ecommerce.async.queue-capacity:100}")
    private int queueCapacity;

    @Bean(name = "orderEventExecutor")
    public Executor orderEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("OrderEvent-Worker-");
        executor.initialize();
        return executor;
    }
}