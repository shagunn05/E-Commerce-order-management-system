package com.amazon.sales.service;

import com.amazon.sales.dto.CategorySalesDto;
import com.amazon.sales.dto.DashboardStatsDto;
import com.amazon.sales.dto.LocationSalesDto;
import com.amazon.sales.model.AmazonSale;
import com.amazon.sales.model.SalesFilter;
import com.amazon.sales.repository.SalesRepository;
import com.amazon.sales.util.CsvExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service generating formatted downloadable CSV reports and business performance summaries.
 */
@Service
@Transactional(readOnly = true)
public class SalesReportService {

    private static final Logger log = LoggerFactory.getLogger(SalesReportService.class);

    private final SalesRepository salesRepository;

    public SalesReportService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    public byte[] generateSalesCsvReport(SalesFilter filter) {
        log.info("Generating CSV sales report");
        List<AmazonSale> sales;
        if (filter != null && hasAnyFilterCriteria(filter)) {
            sales = salesRepository.filter(filter);
        } else {
            sales = salesRepository.findAll();
        }
        return CsvExportUtil.exportSalesToCsv(sales);
    }

    public Map<String, Object> getSalesSummaryReport() {
        log.info("Generating executive sales summary report");
        DashboardStatsDto stats = salesRepository.getDashboardStats();
        List<CategorySalesDto> categories = salesRepository.getCategorySales();
        List<LocationSalesDto> states = salesRepository.getSalesByState(5);

        Map<String, Object> report = new HashMap<>();
        report.put("reportTitle", "Amazon Sales Executive Performance Report");
        report.put("generatedAt", java.time.LocalDateTime.now().toString());
        report.put("kpis", stats);
        report.put("topCategories", categories);
        report.put("topPerformingStates", states);
        return report;
    }

    public List<CategorySalesDto> getCategoryReport() {
        log.info("Generating category performance report");
        return salesRepository.getCategorySales();
    }

    public Map<String, Object> getLocationReport() {
        log.info("Generating location intelligence report");
        Map<String, Object> locationReport = new HashMap<>();
        locationReport.put("topStates", salesRepository.getSalesByState(20));
        locationReport.put("topCities", salesRepository.getSalesByCity(20));
        return locationReport;
    }

    private boolean hasAnyFilterCriteria(SalesFilter f) {
        return (f.getKeyword() != null && !f.getKeyword().isBlank()) ||
                (f.getCategory() != null && !f.getCategory().isBlank()) ||
                (f.getStatus() != null && !f.getStatus().isBlank()) ||
                (f.getFulfilment() != null && !f.getFulfilment().isBlank()) ||
                (f.getStartDate() != null) ||
                (f.getEndDate() != null) ||
                (f.getMinAmount() != null) ||
                (f.getMaxAmount() != null) ||
                (f.getShipCity() != null && !f.getShipCity().isBlank()) ||
                (f.getShipState() != null && !f.getShipState().isBlank());
    }
}
