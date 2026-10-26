package com.ecommerce.web.filter;

import javax.sql.DataSource;
import jakarta.servlet.*;
import java.io.IOException;

public class RequestAuditFilter implements Filter {

    private final DataSource dataSource;


    public RequestAuditFilter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}