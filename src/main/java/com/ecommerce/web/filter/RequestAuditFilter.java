package com.ecommerce.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Custom Servlet Filter demonstrating web request interception:
 * - Filter lifecycle: init(), doFilter(), destroy()
 * - Pre-processing: records start time, HTTP method, client IP, target endpoint
 * - FilterChain execution: proceeds to the next filter/servlet
 * - Post-processing: calculates response latency, adds response headers, records audit log via JDBC
 */
public class RequestAuditFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestAuditFilter.class);
    private final DataSource dataSource;

    public RequestAuditFilter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RequestAuditFilter initialized successfully.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            long startTime = System.currentTimeMillis();
            String uri = httpRequest.getRequestURI();
            String method = httpRequest.getMethod();
            String clientIp = httpRequest.getRemoteAddr();

            try {
                // Proceed with filter chain
                chain.doFilter(request, response);
            } finally {
                long duration = System.currentTimeMillis() - startTime;
                int statusCode = httpResponse.getStatus();

                // Append custom header
                httpResponse.setHeader("X-Execution-Time-Ms", String.valueOf(duration));

                log.debug("HTTP {} {} -> Status: {} [Duration: {} ms]", method, uri, statusCode, duration);

                // Asynchronously or safely persist to audit_logs table via JDBC (skip H2 console assets)
                if (!uri.contains("/h2-console")) {
                    recordAuditLog(uri, method, clientIp, statusCode, duration);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private void recordAuditLog(String endpoint, String method, String clientIp, int statusCode, long durationMs) {
        String sql = "INSERT INTO audit_logs (endpoint, http_method, client_ip, status_code, execution_time_ms) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, endpoint);
            ps.setString(2, method);
            ps.setString(3, clientIp);
            ps.setInt(4, statusCode);
            ps.setLong(5, durationMs);
            ps.executeUpdate();

        } catch (SQLException e) {
            log.warn("Failed to write audit log to database: {}", e.getMessage());
        }
    }

    @Override
    public void destroy() {
        log.info("RequestAuditFilter destroyed.");
    }
}
