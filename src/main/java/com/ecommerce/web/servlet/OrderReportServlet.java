package com.ecommerce.web.servlet;

import com.ecommerce.model.Order;
import com.ecommerce.repository.OrderRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Custom HttpServlet demonstrating foundational Java Web / Servlet concepts:
 * - HttpServlet lifecycle: init(), doGet(), doPost(), destroy()
 * - HttpServletRequest: query parameters, headers, HTTP session management
 * - HttpServletResponse: status codes, content-type headers, character encoding, direct response streaming
 * - Writing CSV and formatted HTML exports without Spring MVC abstraction
 */
public class OrderReportServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(OrderReportServlet.class);
    private final OrderRepository orderRepository;

    public OrderReportServlet(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("OrderReportServlet initialized with Servlet Name: [{}]", config.getServletName());
    }

    /**
     * Handles GET /legacy/order-report?format=csv OR format=html
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String format = req.getParameter("format");
        if (format == null || format.trim().isEmpty()) {
            format = "html";
        }

        // HTTP Session management demonstration
        HttpSession session = req.getSession(true);
        Integer downloadCount = (Integer) session.getAttribute("REPORT_DOWNLOAD_COUNT");
        downloadCount = (downloadCount == null) ? 1 : downloadCount + 1;
        session.setAttribute("REPORT_DOWNLOAD_COUNT", downloadCount);

        List<Order> orders = orderRepository.findAll();

        if ("csv".equalsIgnoreCase(format)) {
            exportCsv(resp, orders);
        } else {
            renderHtml(resp, orders, downloadCount);
        }
    }

    /**
     * Handles POST /legacy/order-report to reset session export statistics
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute("REPORT_DOWNLOAD_COUNT");
        }
        resp.setContentType("application/json");
        resp.getWriter().write("{\"message\": \"Session report counter has been reset successfully.\"}");
    }

    private void exportCsv(HttpServletResponse resp, List<Order> orders) throws IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"orders_export.csv\"");

        PrintWriter writer = resp.getWriter();
        writer.println("OrderNumber,CustomerName,Tier,Status,ItemsCount,TotalAmount,DiscountApplied,CreatedAt");

        for (Order o : orders) {
            writer.printf("%s,%s,%s,%s,%d,%.2f,%.2f,%s%n",
                    o.getOrderNumber(),
                    o.getCustomer() != null ? o.getCustomer().getName() : "N/A",
                    o.getCustomer() != null ? o.getCustomer().getTier().name() : "N/A",
                    o.getStatus().name(),
                    o.getItems().size(),
                    o.getTotalAmount(),
                    o.getDiscountApplied(),
                    o.getCreatedAt()
            );
        }
        writer.flush();
    }

    private void renderHtml(HttpServletResponse resp, List<Order> orders, int sessionCount) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html><html><head><title>Order Report</title>");
        out.println("<style>");
        out.println("body { font-family: Segoe UI, sans-serif; margin: 30px; background: #f8f9fa; color: #333; }");
        out.println("table { border-collapse: collapse; width: 100%; background: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println("th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }");
        out.println("th { background-color: #2c3e50; color: #fff; }");
        out.println("tr:hover { background-color: #f1f1f1; }");
        out.println(".badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; background: #3498db; color: #fff; }");
        out.println("</style></head><body>");

        out.println("<h2>E-Commerce Orders Report (Classic HttpServlet View)</h2>");
        out.println("<p>Session Downloads by You: <strong>" + sessionCount + "</strong> | Export to <a href='?format=csv'>Download CSV</a></p>");
        out.println("<table><thead><tr>");
        out.println("<th>Order #</th><th>Customer</th><th>Tier</th><th>Status</th><th>Items</th><th>Total</th><th>Created</th>");
        out.println("</tr></thead><tbody>");

        for (Order o : orders) {
            out.printf("<tr><td>%s</td><td>%s</td><td><span class='badge'>%s</span></td><td>%s</td><td>%d</td><td>$%.2f</td><td>%s</td></tr>%n",
                    o.getOrderNumber(),
                    o.getCustomer() != null ? o.getCustomer().getName() : "N/A",
                    o.getCustomer() != null ? o.getCustomer().getTier().name() : "N/A",
                    o.getStatus().name(),
                    o.getItems().size(),
                    o.getTotalAmount(),
                    o.getCreatedAt());
        }

        out.println("</tbody></table>");
        out.println("<p style='margin-top:20px; font-size:12px; color:#888;'>Rendered directly via jakarta.servlet.http.HttpServlet</p>");
        out.println("</body></html>");
        out.flush();
    }

    @Override
    public void destroy() {
        log.info("OrderReportServlet destroyed.");
        super.destroy();
    }
}
