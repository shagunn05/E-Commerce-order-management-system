package com.ecommerce.repository;

import com.ecommerce.exception.DatabaseOperationException;
import com.ecommerce.model.PhysicalProduct;
import com.ecommerce.model.Product;
import com.ecommerce.model.SubscriptionProduct;
import com.ecommerce.model.DigitalProduct;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("productJdbcRepository")
@Primary
public class ProductJdbcRepository implements ProductRepository {

    private final DataSource dataSource;

    public ProductJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products (sku, name, category, price, stock_quantity, product_type, weight_kg, download_url, billing_period_months) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getSku());
            ps.setString(2, product.getName());
            ps.setString(3, product.getCategory());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getStockQuantity());
            ps.setString(6, product.getProductType());

            if (product instanceof PhysicalProduct physical) {
                ps.setDouble(7, physical.getWeightKg());
                ps.setNull(8, Types.VARCHAR);
                ps.setInt(9, 0);
            } else if (product instanceof DigitalProduct digital) {
                ps.setDouble(7, 0.0);
                ps.setString(8, digital.getDownloadUrl());
                ps.setInt(9, 0);
            } else if (product instanceof SubscriptionProduct sub) {
                ps.setDouble(7, 0.0);
                ps.setNull(8, Types.VARCHAR);
                ps.setInt(9, sub.getBillingPeriodMonths());
            } else {
                ps.setDouble(7, 0.0);
                ps.setNull(8, Types.VARCHAR);
                ps.setInt(9, 0);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseOperationException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
            return product;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error saving product: " + product.getSku(), e);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToProduct(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error querying product by ID: " + id, e);
        }
    }

    @Override
    public List<Product> findByCategoryIgnoreCase(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE LOWER(category) = LOWER(?)";
        try (var conn = dataSource.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding products by category", e);
        }
        return products;
    }

    @Override
    public List<Product> findByStockQuantityLessThan(int quantity) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock_quantity < ?";
        try (var conn = dataSource.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding products by stock quantity", e);
        }
        return products;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM products WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error checking if product exists by id", e);
        }
        return false;
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        String sql = "SELECT * FROM products WHERE sku = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToProduct(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error querying product by SKU: " + sku, e);
        }
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY id ASC";
        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
            return products;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error querying all products", e);
        }
    }

    @Override
    public List<Product> findByCategory(String category) {
        String sql = "SELECT * FROM products WHERE LOWER(category) = LOWER(?) ORDER BY name ASC";
        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
            return products;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error querying products by category: " + category, e);
        }
    }

    @Override
    public boolean updateStock(Long id, int newStock) {
        String sql = "UPDATE products SET stock_quantity = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newStock);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating product stock ID: " + id, e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting product ID: " + id, e);
        }
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String sku = rs.getString("sku");
        String name = rs.getString("name");
        String category = rs.getString("category");
        double price = rs.getDouble("price");
        int stock = rs.getInt("stock_quantity");
        String type = rs.getString("product_type");

        return switch (type.toUpperCase()) {
            case "PHYSICAL" -> {
                double weight = rs.getDouble("weight_kg");
                yield new PhysicalProduct(id, sku, name, category, price, stock, weight);
            }
            case "DIGITAL" -> {
                String downloadUrl = rs.getString("download_url");
                yield new DigitalProduct(id, sku, name, category, price, stock, downloadUrl);
            }
            case "SUBSCRIPTION" -> {
                int months = rs.getInt("billing_period_months");
                yield new SubscriptionProduct(id, sku, name, category, price, stock, months);
            }
            default -> throw new IllegalStateException("Unknown product type from database: " + type);
        };
    }
}