package com.amazon.sales.controller;

import com.amazon.sales.dto.CategorySalesDto;
import com.amazon.sales.model.SalesFilter;
import com.amazon.sales.service.SalesReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for generating business intelligence and downloadable CSV reports.
 * Base URL: /api/v1/reports
 */
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final SalesReportService reportService;

    public ReportController(SalesReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales/csv")
    public ResponseEntity<byte[]> exportSalesCsv(SalesFilter filter) {
        byte[] csvBytes = reportService.generateSalesCsvReport(filter);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"amazon_sales_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }

    @GetMapping("/sales/summary")
    public ResponseEntity<Map<String, Object>> getSalesSummary() {
        return ResponseEntity.ok(reportService.getSalesSummaryReport());
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategorySalesDto>> getCategoryReport() {
        return ResponseEntity.ok(reportService.getCategoryReport());
    }

    @GetMapping("/location")
    public ResponseEntity<Map<String, Object>> getLocationReport() {
        return ResponseEntity.ok(reportService.getLocationReport());
    }
}
