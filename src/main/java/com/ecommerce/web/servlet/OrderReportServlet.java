package com.ecommerce.web.servlet;

import com.ecommerce.repository.OrderRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderReportServlet extends HttpServlet {

    private final OrderRepository orderRepository;


    public OrderReportServlet(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.getWriter().write("Order Report Generated!");
    }
}