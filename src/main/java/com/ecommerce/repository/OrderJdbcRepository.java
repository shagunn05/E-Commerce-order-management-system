package com.ecommerce.repository;

import com.ecommerce.exception.DatabaseOperationException;
import com.ecommerce.model.*;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderJdbcRepository implements OrderRepository {

    private final DataSource dataSource;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderJdbcRepository(DataSource dataSource,
                               ProductRepository productRepository,
                               CustomerRepository customerRepository) {
        this.dataSource = dataSource;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Order save(Order order) {
        String insertOrderSql = "INSERT INTO orders (order_number, customer_id, status, total_amount, discount_applied, priority_level) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // Begin ACID Transaction

            Long orderId;
            try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getOrderNumber());
                psOrder.setLong(2, order.getCustomer().getId());
                psOrder.setString(3, order.getStatus().name());
                psOrder.setDouble(4, order.getTotalAmount());
                psOrder.setDouble(5, order.getDiscountApplied());
                psOrder.setInt(6, order.getPriorityLevel());

                psOrder.executeUpdate();
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getLong(1);
                        order.setId(orderId);
                    } else {
                        throw new DatabaseOperationException("Failed to obtain generated ID for Order.");
                    }
                }
            }

            if (!order.getItems().isEmpty()) {
                try (PreparedStatement psItems = conn.prepareStatement(insertItemSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (OrderItem item : order.getItems()) {
                        item.setOrderId(orderId);
                        psItems.setLong(1, orderId);
                        psItems.setLong(2, item.getProduct().getId());
                        psItems.setInt(3, item.getQuantity());
                        psItems.setDouble(4, item.getUnitPrice());
                        psItems.setDouble(5, item.getSubtotal());
                        psItems.addBatch();
                    }
                    psItems.executeBatch();

                    try (ResultSet rsKeys = psItems.getGeneratedKeys()) {
                        int index = 0;
                        while (rsKeys.next() && index < order.getItems().size()) {
                            order.getItems().get(index).setId(rsKeys.getLong(1));
                            index++;
                        }
                    }
                }
            }

            conn.commit(); // Commit Transaction
            return order;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new DatabaseOperationException("Failed to rollback transaction", rollbackEx);
                }
            }
            throw new DatabaseOperationException("Failed to persist order in transaction: " + order.getOrderNumber(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    loadOrderItems(conn, order);
                    return Optional.of(order);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding order by ID: " + id, e);
        }
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        String sql = "SELECT * FROM orders WHERE order_number = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, orderNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapRowToOrder(rs);
                    loadOrderItems(conn, order);
                    return Optional.of(order);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding order by number: " + orderNumber, e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY id DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
            for (Order order : orders) {
                loadOrderItems(conn, order);
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving all orders", e);
        }
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY id DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
            for (Order order : orders) {
                loadOrderItems(conn, order);
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving orders for customer ID: " + customerId, e);
        }
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY priority_level DESC, created_at ASC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
            for (Order order : orders) {
                loadOrderItems(conn, order);
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving orders by status: " + status, e);
        }
    }

    @Override
    public boolean updateStatus(Long orderId, OrderStatus newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.name());
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating order status ID: " + orderId, e);
        }
    }

    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderNumber(rs.getString("order_number"));

        Long customerId = rs.getLong("customer_id");
        Customer customer = customerRepository.findById(customerId).orElse(null);
        order.setCustomer(customer);

        order.setStatus(OrderStatus.valueOf(rs.getString("status").toUpperCase()));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setDiscountApplied(rs.getDouble("discount_applied"));
        order.setPriorityLevel(rs.getInt("priority_level"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            order.setCreatedAt(ts.toLocalDateTime());
        }
        return order;
    }

    private void loadOrderItems(Connection conn, Order order) throws SQLException {
        String itemSql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItem> items = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
            ps.setLong(1, order.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));

                    Long productId = rs.getLong("product_id");
                    Product product = productRepository.findById(productId).orElse(null);
                    item.setProduct(product);

                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setSubtotal(rs.getDouble("subtotal"));
                    items.add(item);
                }
            }
        }
        order.setItems(items);
    }
}