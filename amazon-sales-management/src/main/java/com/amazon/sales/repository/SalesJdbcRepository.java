package com.amazon.sales.repository;

import com.amazon.sales.dto.*;
import com.amazon.sales.exception.DatabaseOperationException;
import com.amazon.sales.model.AmazonSale;
import com.amazon.sales.model.SalesFilter;
import com.amazon.sales.util.SalesRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

/**
 * Spring JDBC implementation of SalesRepository communicating with PostgreSQL.
 */
@Repository
public class SalesJdbcRepository implements SalesRepository {

    private static final Logger log = LoggerFactory.getLogger(SalesJdbcRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final SalesRowMapper rowMapper = new SalesRowMapper();

    public SalesJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "index_id", "order_id", "date", "status", "fulfilment", "sales_channel",
            "style", "sku", "category", "size", "qty", "amount", "ship_city", "ship_state"
    );

    @Override
    public List<AmazonSale> findAll() {
        String sql = "SELECT * FROM amazon_sales ORDER BY index_id DESC LIMIT 1000";
        try {
            return jdbcTemplate.query(sql, rowMapper);
        } catch (DataAccessException ex) {
            log.error("Error executing findAll: {}", ex.getMessage());
            throw new DatabaseOperationException("Failed to retrieve sales records", ex);
        }
    }

    @Override
    public Optional<AmazonSale> findById(Long id) {
        String sql = "SELECT * FROM amazon_sales WHERE index_id = ?";
        try {
            List<AmazonSale> results = jdbcTemplate.query(sql, rowMapper, id);
            return results.stream().findFirst();
        } catch (DataAccessException ex) {
            log.error("Error executing findById for id {}: {}", id, ex.getMessage());
            throw new DatabaseOperationException("Failed to retrieve sale record by id: " + id, ex);
        }
    }

    @Override
    public AmazonSale save(AmazonSale sale) {
        String sql = "INSERT INTO amazon_sales (" +
                "order_id, date, status, fulfilment, sales_channel, ship_service_level, " +
                "style, sku, category, size, asin, courier_status, qty, currency, " +
                "amount, ship_city, ship_state, ship_postal_code, ship_country, " +
                "promotion_ids, b2b, fulfilled_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, sale.getOrderId());
                ps.setDate(2, sale.getDate() != null ? Date.valueOf(sale.getDate()) : null);
                ps.setString(3, sale.getStatus());
                ps.setString(4, sale.getFulfilment());
                ps.setString(5, sale.getSalesChannel());
                ps.setString(6, sale.getShipServiceLevel());
                ps.setString(7, sale.getStyle());
                ps.setString(8, sale.getSku());
                ps.setString(9, sale.getCategory());
                ps.setString(10, sale.getSize());
                ps.setString(11, sale.getAsin());
                ps.setString(12, sale.getCourierStatus());
                ps.setInt(13, sale.getQty() != null ? sale.getQty() : 1);
                ps.setString(14, sale.getCurrency() != null ? sale.getCurrency() : "INR");
                ps.setBigDecimal(15, sale.getAmount() != null ? sale.getAmount() : BigDecimal.ZERO);
                ps.setString(16, sale.getShipCity());
                ps.setString(17, sale.getShipState());
                ps.setString(18, sale.getShipPostalCode());
                ps.setString(19, sale.getShipCountry() != null ? sale.getShipCountry() : "IN");
                ps.setString(20, sale.getPromotionIds());
                ps.setBoolean(21, sale.getB2b() != null && sale.getB2b());
                ps.setString(22, sale.getFulfilledBy());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                sale.setIndexId(key.longValue());
            }
            return sale;
        } catch (DataAccessException ex) {
            log.error("Error executing save: {}", ex.getMessage());
            throw new DatabaseOperationException("Failed to save new sale record: " + ex.getMessage(), ex);
        }
    }

    @Override
    public AmazonSale update(Long id, AmazonSale sale) {
        String sql = "UPDATE amazon_sales SET " +
                "order_id = ?, date = ?, status = ?, fulfilment = ?, sales_channel = ?, " +
                "ship_service_level = ?, style = ?, sku = ?, category = ?, size = ?, " +
                "asin = ?, courier_status = ?, qty = ?, currency = ?, amount = ?, " +
                "ship_city = ?, ship_state = ?, ship_postal_code = ?, ship_country = ?, " +
                "promotion_ids = ?, b2b = ?, fulfilled_by = ? WHERE index_id = ?";

        try {
            int rows = jdbcTemplate.update(sql,
                    sale.getOrderId(),
                    sale.getDate() != null ? Date.valueOf(sale.getDate()) : null,
                    sale.getStatus(),
                    sale.getFulfilment(),
                    sale.getSalesChannel(),
                    sale.getShipServiceLevel(),
                    sale.getStyle(),
                    sale.getSku(),
                    sale.getCategory(),
                    sale.getSize(),
                    sale.getAsin(),
                    sale.getCourierStatus(),
                    sale.getQty() != null ? sale.getQty() : 1,
                    sale.getCurrency() != null ? sale.getCurrency() : "INR",
                    sale.getAmount() != null ? sale.getAmount() : BigDecimal.ZERO,
                    sale.getShipCity(),
                    sale.getShipState(),
                    sale.getShipPostalCode(),
                    sale.getShipCountry() != null ? sale.getShipCountry() : "IN",
                    sale.getPromotionIds(),
                    sale.getB2b() != null && sale.getB2b(),
                    sale.getFulfilledBy(),
                    id
            );

            if (rows == 0) {
                return null;
            }
            sale.setIndexId(id);
            return sale;
        } catch (DataAccessException ex) {
            log.error("Error executing update for id {}: {}", id, ex.getMessage());
            throw new DatabaseOperationException("Failed to update sale record with id: " + id, ex);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM amazon_sales WHERE index_id = ?";
        try {
            int rows = jdbcTemplate.update(sql, id);
            return rows > 0;
        } catch (DataAccessException ex) {
            log.error("Error executing delete for id {}: {}", id, ex.getMessage());
            throw new DatabaseOperationException("Failed to delete sale record with id: " + id, ex);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(1) FROM amazon_sales WHERE index_id = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return count != null && count > 0;
        } catch (DataAccessException ex) {
            return false;
        }
    }

    @Override
    public List<AmazonSale> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        String sql = "SELECT * FROM amazon_sales WHERE " +
                "LOWER(order_id) LIKE ? OR " +
                "LOWER(sku) LIKE ? OR " +
                "LOWER(style) LIKE ? OR " +
                "LOWER(category) LIKE ? OR " +
                "LOWER(asin) LIKE ? OR " +
                "LOWER(status) LIKE ? OR " +
                "LOWER(ship_city) LIKE ? OR " +
                "LOWER(ship_state) LIKE ? " +
                "ORDER BY index_id DESC LIMIT 500";

        try {
            return jdbcTemplate.query(sql, rowMapper, pattern, pattern, pattern, pattern, pattern, pattern, pattern, pattern);
        } catch (DataAccessException ex) {
            log.error("Error executing search for '{}': {}", keyword, ex.getMessage());
            throw new DatabaseOperationException("Search query failed", ex);
        }
    }

    @Override
    public List<AmazonSale> filter(SalesFilter filter) {
        QueryBuilder qb = buildFilterQuery(filter, false);
        try {
            return jdbcTemplate.query(qb.sql, rowMapper, qb.params.toArray());
        } catch (DataAccessException ex) {
            log.error("Error executing filter query: {}", ex.getMessage());
            throw new DatabaseOperationException("Filter query failed", ex);
        }
    }

    @Override
    public PageResponseDto<AmazonSale> findPaginated(SalesFilter filter) {
        QueryBuilder countQb = buildFilterQuery(filter, true);
        Long totalElements = 0L;
        try {
            Long count = jdbcTemplate.queryForObject(countQb.sql, Long.class, countQb.params.toArray());
            totalElements = (count != null) ? count : 0L;
        } catch (DataAccessException ex) {
            log.error("Error counting paginated records: {}", ex.getMessage());
        }

        QueryBuilder dataQb = buildFilterQuery(filter, false);
        // Append pagination
        String safeSort = sanitizeSortColumn(filter.getSortBy());
        String safeDirection = filter.getSortDirection();
        int offset = filter.getPage() * filter.getSize();

        dataQb.sql += " ORDER BY " + safeSort + " " + safeDirection + " LIMIT ? OFFSET ?";
        dataQb.params.add(filter.getSize());
        dataQb.params.add(offset);

        try {
            List<AmazonSale> content = jdbcTemplate.query(dataQb.sql, rowMapper, dataQb.params.toArray());
            return new PageResponseDto<>(content, filter.getPage(), filter.getSize(), totalElements);
        } catch (DataAccessException ex) {
            log.error("Error executing findPaginated: {}", ex.getMessage());
            throw new DatabaseOperationException("Failed to retrieve paginated records", ex);
        }
    }

    @Override
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        try {
            String countSql = "SELECT COUNT(1) FROM amazon_sales";
            Long totalRecords = jdbcTemplate.queryForObject(countSql, Long.class);
            stats.setTotalRecords(totalRecords != null ? totalRecords : 0L);

            String ordersSql = "SELECT COUNT(DISTINCT order_id) FROM amazon_sales";
            Long totalOrders = jdbcTemplate.queryForObject(ordersSql, Long.class);
            stats.setTotalOrders(totalOrders != null ? totalOrders : 0L);

            String revenueSql = "SELECT COALESCE(SUM(amount), 0) FROM amazon_sales WHERE status NOT ILIKE '%Cancel%'";
            BigDecimal totalRev = jdbcTemplate.queryForObject(revenueSql, BigDecimal.class);
            stats.setTotalRevenue(totalRev != null ? totalRev : BigDecimal.ZERO);

            String unitsSql = "SELECT COALESCE(SUM(qty), 0) FROM amazon_sales WHERE status NOT ILIKE '%Cancel%'";
            Long totalUnits = jdbcTemplate.queryForObject(unitsSql, Long.class);
            stats.setTotalUnits(totalUnits != null ? totalUnits : 0L);

            if (stats.getTotalOrders() > 0 && stats.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
                stats.setAverageOrderValue(stats.getTotalRevenue().divide(BigDecimal.valueOf(stats.getTotalOrders()), 2, RoundingMode.HALF_UP));
            }

            if (stats.getTotalRecords() > 0) {
                String deliveredSql = "SELECT COUNT(1) FROM amazon_sales WHERE status ILIKE '%Deliver%' OR status ILIKE '%Shipped%'";
                Long delivered = jdbcTemplate.queryForObject(deliveredSql, Long.class);
                if (delivered != null) {
                    stats.setDeliveredRate(Math.round(((double) delivered / stats.getTotalRecords()) * 1000.0) / 10.0);
                }

                String cancelledSql = "SELECT COUNT(1) FROM amazon_sales WHERE status ILIKE '%Cancel%'";
                Long cancelled = jdbcTemplate.queryForObject(cancelledSql, Long.class);
                if (cancelled != null) {
                    stats.setCancelledRate(Math.round(((double) cancelled / stats.getTotalRecords()) * 1000.0) / 10.0);
                }

                String b2bSql = "SELECT COUNT(1) FROM amazon_sales WHERE b2b = TRUE";
                Long b2bCount = jdbcTemplate.queryForObject(b2bSql, Long.class);
                if (b2bCount != null) {
                    stats.setB2bPercentage(Math.round(((double) b2bCount / stats.getTotalRecords()) * 1000.0) / 10.0);
                }
            }
        } catch (DataAccessException ex) {
            log.error("Error calculating dashboard stats: {}", ex.getMessage());
        }

        return stats;
    }

    @Override
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM amazon_sales WHERE status NOT ILIKE '%Cancel%'";
        try {
            BigDecimal res = jdbcTemplate.queryForObject(sql, BigDecimal.class);
            return res != null ? res : BigDecimal.ZERO;
        } catch (DataAccessException ex) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Long getTotalOrders() {
        String sql = "SELECT COUNT(DISTINCT order_id) FROM amazon_sales";
        try {
            Long res = jdbcTemplate.queryForObject(sql, Long.class);
            return res != null ? res : 0L;
        } catch (DataAccessException ex) {
            return 0L;
        }
    }

    @Override
    public Long getTotalUnits() {
        String sql = "SELECT COALESCE(SUM(qty), 0) FROM amazon_sales WHERE status NOT ILIKE '%Cancel%'";
        try {
            Long res = jdbcTemplate.queryForObject(sql, Long.class);
            return res != null ? res : 0L;
        } catch (DataAccessException ex) {
            return 0L;
        }
    }

    @Override
    public BigDecimal getAverageOrderValue() {
        Long orders = getTotalOrders();
        BigDecimal revenue = getTotalRevenue();
        if (orders != null && orders > 0) {
            return revenue.divide(BigDecimal.valueOf(orders), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<DailySalesDto> getSalesOverview() {
        String sql = "SELECT date, COALESCE(SUM(amount), 0) as revenue, COUNT(DISTINCT order_id) as order_count, " +
                "COALESCE(SUM(qty), 0) as units FROM amazon_sales " +
                "WHERE date IS NOT NULL AND status NOT ILIKE '%Cancel%' " +
                "GROUP BY date ORDER BY date ASC LIMIT 30";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> new DailySalesDto(
                    rs.getDate("date").toLocalDate(),
                    rs.getBigDecimal("revenue"),
                    rs.getLong("order_count"),
                    rs.getLong("units")
            ));
        } catch (DataAccessException ex) {
            log.error("Error executing getSalesOverview: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<CategorySalesDto> getCategorySales() {
        BigDecimal totalRev = getTotalRevenue();
        String sql = "SELECT COALESCE(category, 'Uncategorized') as category, " +
                "COUNT(DISTINCT order_id) as total_orders, " +
                "COALESCE(SUM(qty), 0) as total_units, " +
                "COALESCE(SUM(amount), 0) as total_revenue, " +
                "COALESCE(AVG(amount), 0) as avg_price " +
                "FROM amazon_sales " +
                "GROUP BY category ORDER BY total_revenue DESC";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                BigDecimal catRev = rs.getBigDecimal("total_revenue");
                double pct = 0.0;
                if (totalRev.compareTo(BigDecimal.ZERO) > 0 && catRev != null) {
                    pct = catRev.divide(totalRev, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
                }
                return new CategorySalesDto(
                        rs.getString("category"),
                        rs.getLong("total_orders"),
                        rs.getLong("total_units"),
                        catRev,
                        rs.getBigDecimal("avg_price").setScale(2, RoundingMode.HALF_UP),
                        Math.round(pct * 10.0) / 10.0
                );
            });
        } catch (DataAccessException ex) {
            log.error("Error executing getCategorySales: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<TopProductDto> getTopProducts(int limit) {
        String sql = "SELECT COALESCE(sku, 'Unknown') as sku, " +
                "MAX(style) as style, " +
                "MAX(category) as category, " +
                "COALESCE(SUM(qty), 0) as units_sold, " +
                "COALESCE(SUM(amount), 0) as revenue, " +
                "COALESCE(AVG(amount), 0) as avg_price " +
                "FROM amazon_sales " +
                "GROUP BY sku ORDER BY revenue DESC LIMIT ?";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> new TopProductDto(
                    rs.getString("sku"),
                    rs.getString("style"),
                    rs.getString("category"),
                    rs.getLong("units_sold"),
                    rs.getBigDecimal("revenue"),
                    rs.getBigDecimal("avg_price").setScale(2, RoundingMode.HALF_UP)
            ), limit > 0 ? limit : 10);
        } catch (DataAccessException ex) {
            log.error("Error executing getTopProducts: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<LocationSalesDto> getSalesByState(int limit) {
        BigDecimal totalRev = getTotalRevenue();
        String sql = "SELECT COALESCE(ship_state, 'Unknown') as location, " +
                "COUNT(DISTINCT order_id) as total_orders, " +
                "COALESCE(SUM(qty), 0) as total_units, " +
                "COALESCE(SUM(amount), 0) as total_revenue " +
                "FROM amazon_sales " +
                "WHERE ship_state IS NOT NULL AND ship_state <> '' " +
                "GROUP BY ship_state ORDER BY total_revenue DESC LIMIT ?";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                BigDecimal rev = rs.getBigDecimal("total_revenue");
                double pct = 0.0;
                if (totalRev.compareTo(BigDecimal.ZERO) > 0 && rev != null) {
                    pct = rev.divide(totalRev, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
                }
                return new LocationSalesDto(
                        rs.getString("location"),
                        rs.getLong("total_orders"),
                        rs.getLong("total_units"),
                        rev,
                        Math.round(pct * 10.0) / 10.0
                );
            }, limit > 0 ? limit : 10);
        } catch (DataAccessException ex) {
            log.error("Error executing getSalesByState: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<LocationSalesDto> getSalesByCity(int limit) {
        BigDecimal totalRev = getTotalRevenue();
        String sql = "SELECT COALESCE(ship_city, 'Unknown') as location, " +
                "COUNT(DISTINCT order_id) as total_orders, " +
                "COALESCE(SUM(qty), 0) as total_units, " +
                "COALESCE(SUM(amount), 0) as total_revenue " +
                "FROM amazon_sales " +
                "WHERE ship_city IS NOT NULL AND ship_city <> '' " +
                "GROUP BY ship_city ORDER BY total_revenue DESC LIMIT ?";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                BigDecimal rev = rs.getBigDecimal("total_revenue");
                double pct = 0.0;
                if (totalRev.compareTo(BigDecimal.ZERO) > 0 && rev != null) {
                    pct = rev.divide(totalRev, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
                }
                return new LocationSalesDto(
                        rs.getString("location"),
                        rs.getLong("total_orders"),
                        rs.getLong("total_units"),
                        rev,
                        Math.round(pct * 10.0) / 10.0
                );
            }, limit > 0 ? limit : 10);
        } catch (DataAccessException ex) {
            log.error("Error executing getSalesByCity: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<StatusDistributionDto> getSalesBySize() {
        return getDistributionGeneric("size", "size IS NOT NULL AND size <> ''");
    }

    @Override
    public List<StatusDistributionDto> getSalesByChannel() {
        return getDistributionGeneric("sales_channel", "sales_channel IS NOT NULL AND sales_channel <> ''");
    }

    @Override
    public List<StatusDistributionDto> getFulfilmentAnalysis() {
        return getDistributionGeneric("fulfilment", "fulfilment IS NOT NULL AND fulfilment <> ''");
    }

    @Override
    public List<StatusDistributionDto> getOrderStatusDistribution() {
        return getDistributionGeneric("status", "status IS NOT NULL AND status <> ''");
    }

    @Override
    public List<StatusDistributionDto> getCourierStatusDistribution() {
        return getDistributionGeneric("courier_status", "courier_status IS NOT NULL AND courier_status <> ''");
    }

    @Override
    public List<MonthlyRevenueDto> getMonthlyRevenue() {
        // Universal SQL for both Postgres and H2
        String sql = "SELECT EXTRACT(YEAR FROM date) as yr, EXTRACT(MONTH FROM date) as mo, " +
                "COALESCE(SUM(amount), 0) as revenue, COUNT(DISTINCT order_id) as order_count, " +
                "COALESCE(SUM(qty), 0) as units FROM amazon_sales " +
                "WHERE date IS NOT NULL AND status NOT ILIKE '%Cancel%' " +
                "GROUP BY EXTRACT(YEAR FROM date), EXTRACT(MONTH FROM date) " +
                "ORDER BY yr ASC, mo ASC";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                int yr = rs.getInt("yr");
                int mo = rs.getInt("mo");
                String monthYear = String.format("%d-%02d", yr, mo);
                return new MonthlyRevenueDto(
                        monthYear,
                        rs.getBigDecimal("revenue"),
                        rs.getLong("order_count"),
                        rs.getLong("units")
                );
            });
        } catch (DataAccessException ex) {
            log.error("Error executing getMonthlyRevenue: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<DailySalesDto> getDailySales(int limit) {
        String sql = "SELECT date, COALESCE(SUM(amount), 0) as revenue, COUNT(DISTINCT order_id) as order_count, " +
                "COALESCE(SUM(qty), 0) as units FROM amazon_sales " +
                "WHERE date IS NOT NULL AND status NOT ILIKE '%Cancel%' " +
                "GROUP BY date ORDER BY date DESC LIMIT ?";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> new DailySalesDto(
                    rs.getDate("date").toLocalDate(),
                    rs.getBigDecimal("revenue"),
                    rs.getLong("order_count"),
                    rs.getLong("units")
            ), limit > 0 ? limit : 30);
        } catch (DataAccessException ex) {
            log.error("Error executing getDailySales: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<StatusDistributionDto> getDistributionGeneric(String column, String filterCondition) {
        BigDecimal totalRev = getTotalRevenue();
        String sql = "SELECT COALESCE(" + column + ", 'Unknown') as name, " +
                "COUNT(*) as total_count, " +
                "COALESCE(SUM(amount), 0) as revenue " +
                "FROM amazon_sales " +
                "WHERE " + filterCondition + " " +
                "GROUP BY " + column + " ORDER BY revenue DESC";

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                BigDecimal rev = rs.getBigDecimal("revenue");
                double pct = 0.0;
                if (totalRev.compareTo(BigDecimal.ZERO) > 0 && rev != null) {
                    pct = rev.divide(totalRev, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
                }
                return new StatusDistributionDto(
                        rs.getString("name"),
                        rs.getLong("total_count"),
                        rev,
                        Math.round(pct * 10.0) / 10.0
                );
            });
        } catch (DataAccessException ex) {
            log.error("Error executing distribution query for {}: {}", column, ex.getMessage());
            return Collections.emptyList();
        }
    }

    private static class QueryBuilder {
        String sql;
        List<Object> params = new ArrayList<>();
    }

    private QueryBuilder buildFilterQuery(SalesFilter filter, boolean countOnly) {
        QueryBuilder qb = new QueryBuilder();
        StringBuilder sql = new StringBuilder();

        if (countOnly) {
            sql.append("SELECT COUNT(1) FROM amazon_sales WHERE 1=1");
        } else {
            sql.append("SELECT * FROM amazon_sales WHERE 1=1");
        }

        if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
            String p = "%" + filter.getKeyword().trim().toLowerCase() + "%";
            sql.append(" AND (LOWER(order_id) LIKE ? OR LOWER(sku) LIKE ? OR LOWER(style) LIKE ? OR LOWER(category) LIKE ? OR LOWER(ship_city) LIKE ? OR LOWER(ship_state) LIKE ?)");
            qb.params.add(p);
            qb.params.add(p);
            qb.params.add(p);
            qb.params.add(p);
            qb.params.add(p);
            qb.params.add(p);
        }

        if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
            sql.append(" AND LOWER(category) = ?");
            qb.params.add(filter.getCategory().trim().toLowerCase());
        }

        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            sql.append(" AND LOWER(status) = ?");
            qb.params.add(filter.getStatus().trim().toLowerCase());
        }

        if (filter.getFulfilment() != null && !filter.getFulfilment().isBlank()) {
            sql.append(" AND LOWER(fulfilment) = ?");
            qb.params.add(filter.getFulfilment().trim().toLowerCase());
        }

        if (filter.getSalesChannel() != null && !filter.getSalesChannel().isBlank()) {
            sql.append(" AND LOWER(sales_channel) = ?");
            qb.params.add(filter.getSalesChannel().trim().toLowerCase());
        }

        if (filter.getShipServiceLevel() != null && !filter.getShipServiceLevel().isBlank()) {
            sql.append(" AND LOWER(ship_service_level) = ?");
            qb.params.add(filter.getShipServiceLevel().trim().toLowerCase());
        }

        if (filter.getCourierStatus() != null && !filter.getCourierStatus().isBlank()) {
            sql.append(" AND LOWER(courier_status) = ?");
            qb.params.add(filter.getCourierStatus().trim().toLowerCase());
        }

        if (filter.getB2b() != null) {
            sql.append(" AND b2b = ?");
            qb.params.add(filter.getB2b());
        }

        if (filter.getMinAmount() != null) {
            sql.append(" AND amount >= ?");
            qb.params.add(filter.getMinAmount());
        }

        if (filter.getMaxAmount() != null) {
            sql.append(" AND amount <= ?");
            qb.params.add(filter.getMaxAmount());
        }

        if (filter.getStartDate() != null) {
            sql.append(" AND date >= ?");
            qb.params.add(Date.valueOf(filter.getStartDate()));
        }

        if (filter.getEndDate() != null) {
            sql.append(" AND date <= ?");
            qb.params.add(Date.valueOf(filter.getEndDate()));
        }

        if (filter.getShipCity() != null && !filter.getShipCity().isBlank()) {
            sql.append(" AND LOWER(ship_city) = ?");
            qb.params.add(filter.getShipCity().trim().toLowerCase());
        }

        if (filter.getShipState() != null && !filter.getShipState().isBlank()) {
            sql.append(" AND LOWER(ship_state) = ?");
            qb.params.add(filter.getShipState().trim().toLowerCase());
        }

        qb.sql = sql.toString();
        return qb;
    }

    private String sanitizeSortColumn(String sortBy) {
        if (sortBy != null && ALLOWED_SORT_COLUMNS.contains(sortBy.toLowerCase())) {
            return sortBy.toLowerCase();
        }
        return "index_id";
    }
}
