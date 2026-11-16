package com.amazon.sales.util;

import com.amazon.sales.model.AmazonSale;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Utility for exporting sales records to RFC-4180 compliant CSV format.
 */
public final class CsvExportUtil {

    private CsvExportUtil() {}

    private static final String CSV_HEADER = "Index ID,Order ID,Date,Status,Fulfilment,Sales Channel,Ship Service Level,Style,SKU,Category,Size,ASIN,Courier Status,Qty,Currency,Amount,Ship City,Ship State,Ship Postal Code,Ship Country,Promotion IDs,B2B,Fulfilled By\r\n";

    public static byte[] exportSalesToCsv(List<AmazonSale> sales) {
        StringBuilder sb = new StringBuilder(CSV_HEADER);

        if (sales != null) {
            for (AmazonSale sale : sales) {
                appendField(sb, sale.getIndexId());
                sb.append(',');
                appendField(sb, sale.getOrderId());
                sb.append(',');
                appendField(sb, sale.getDate());
                sb.append(',');
                appendField(sb, sale.getStatus());
                sb.append(',');
                appendField(sb, sale.getFulfilment());
                sb.append(',');
                appendField(sb, sale.getSalesChannel());
                sb.append(',');
                appendField(sb, sale.getShipServiceLevel());
                sb.append(',');
                appendField(sb, sale.getStyle());
                sb.append(',');
                appendField(sb, sale.getSku());
                sb.append(',');
                appendField(sb, sale.getCategory());
                sb.append(',');
                appendField(sb, sale.getSize());
                sb.append(',');
                appendField(sb, sale.getAsin());
                sb.append(',');
                appendField(sb, sale.getCourierStatus());
                sb.append(',');
                appendField(sb, sale.getQty());
                sb.append(',');
                appendField(sb, sale.getCurrency());
                sb.append(',');
                appendField(sb, sale.getAmount());
                sb.append(',');
                appendField(sb, sale.getShipCity());
                sb.append(',');
                appendField(sb, sale.getShipState());
                sb.append(',');
                appendField(sb, sale.getShipPostalCode());
                sb.append(',');
                appendField(sb, sale.getShipCountry());
                sb.append(',');
                appendField(sb, sale.getPromotionIds());
                sb.append(',');
                appendField(sb, sale.getB2b());
                sb.append(',');
                appendField(sb, sale.getFulfilledBy());
                sb.append("\r\n");
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void appendField(StringBuilder sb, Object value) {
        if (value == null) {
            return;
        }
        String str = String.valueOf(value);
        boolean containsSpecial = str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r");

        if (containsSpecial) {
            sb.append('"');
            sb.append(str.replace("\"", "\"\""));
            sb.append('"');
        } else {
            sb.append(str);
        }
    }
}
