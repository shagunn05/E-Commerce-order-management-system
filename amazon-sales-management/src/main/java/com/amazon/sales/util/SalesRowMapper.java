package com.amazon.sales.util;

import com.amazon.sales.model.AmazonSale;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Maps PostgreSQL result set rows to AmazonSale domain models.
 * Handles null checking for numeric, date, and boolean columns.
 */
public class SalesRowMapper implements RowMapper<AmazonSale> {

    @Override
    public AmazonSale mapRow(ResultSet rs, int rowNum) throws SQLException {
        AmazonSale sale = new AmazonSale();

        sale.setIndexId(rs.getLong("index_id"));
        sale.setOrderId(rs.getString("order_id"));

        Date dateVal = rs.getDate("date");
        sale.setDate(dateVal != null ? dateVal.toLocalDate() : null);

        sale.setStatus(rs.getString("status"));
        sale.setFulfilment(rs.getString("fulfilment"));
        sale.setSalesChannel(rs.getString("sales_channel"));
        sale.setShipServiceLevel(rs.getString("ship_service_level"));
        sale.setStyle(rs.getString("style"));
        sale.setSku(rs.getString("sku"));
        sale.setCategory(rs.getString("category"));
        sale.setSize(rs.getString("size"));
        sale.setAsin(rs.getString("asin"));
        sale.setCourierStatus(rs.getString("courier_status"));

        int qty = rs.getInt("qty");
        sale.setQty(rs.wasNull() ? 1 : qty);

        sale.setCurrency(rs.getString("currency"));

        BigDecimal amount = rs.getBigDecimal("amount");
        sale.setAmount(amount != null ? amount : BigDecimal.ZERO);

        sale.setShipCity(rs.getString("ship_city"));
        sale.setShipState(rs.getString("ship_state"));
        sale.setShipPostalCode(rs.getString("ship_postal_code"));
        sale.setShipCountry(rs.getString("ship_country"));
        sale.setPromotionIds(rs.getString("promotion_ids"));

        boolean b2b = rs.getBoolean("b2b");
        sale.setB2b(rs.wasNull() ? false : b2b);

        sale.setFulfilledBy(rs.getString("fulfilled_by"));

        return sale;
    }
}
