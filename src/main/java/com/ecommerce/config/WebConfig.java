package com.ecommerce.config;

import com.ecommerce.repository.OrderRepository;
import com.ecommerce.web.filter.RequestAuditFilter;
import com.ecommerce.web.servlet.OrderReportServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class WebConfig {

    @Bean
    public ServletRegistrationBean<OrderReportServlet> orderReportServletRegistration(OrderRepository orderRepository) {
        ServletRegistrationBean<OrderReportServlet> registration = new ServletRegistrationBean<>(
                new OrderReportServlet(orderRepository),
                "/legacy/order-report"
        );
        registration.setLoadOnStartup(1);
        registration.setName("OrderReportServlet");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestAuditFilter> requestAuditFilterRegistration(DataSource dataSource) {
        FilterRegistrationBean<RequestAuditFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestAuditFilter(dataSource));
        registration.addUrlPatterns("/*");
        registration.setName("RequestAuditFilter");
        registration.setOrder(1);
        return registration;
    }
}