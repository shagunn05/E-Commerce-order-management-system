package com.amazon.sales;

import com.amazon.sales.dto.DashboardStatsDto;
import com.amazon.sales.dto.SalesRequestDto;
import com.amazon.sales.dto.SalesResponseDto;
import com.amazon.sales.model.AmazonSale;
import com.amazon.sales.repository.SalesRepository;
import com.amazon.sales.service.SalesAnalyticsService;
import com.amazon.sales.service.SalesService;
import com.amazon.sales.util.CsvExportUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmazonSalesApplicationTests {

    @Mock
    private SalesRepository salesRepository;

    private SalesService salesService;
    private SalesAnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        salesService = new SalesService(salesRepository);
        analyticsService = new SalesAnalyticsService(salesRepository);
    }

    @Test
    @DisplayName("Should create and return a mapped AmazonSale")
    void shouldCreateSale() {
        SalesRequestDto request = new SalesRequestDto();
        request.setOrderId("TEST-1234");
        request.setDate(LocalDate.now());
        request.setStatus("Shipped");
        request.setSku("SKU-TEST-1");
        request.setCategory("Kurta");
        request.setAmount(new BigDecimal("499.00"));
        request.setQty(1);

        AmazonSale savedSale = new AmazonSale();
        savedSale.setIndexId(101L);
        savedSale.setOrderId("TEST-1234");
        savedSale.setDate(request.getDate());
        savedSale.setStatus("Shipped");
        savedSale.setSku("SKU-TEST-1");
        savedSale.setCategory("Kurta");
        savedSale.setAmount(new BigDecimal("499.00"));
        savedSale.setQty(1);

        when(salesRepository.save(any(AmazonSale.class))).thenReturn(savedSale);

        SalesResponseDto result = salesService.createSale(request);

        assertThat(result).isNotNull();
        assertThat(result.getIndexId()).isEqualTo(101L);
        assertThat(result.getOrderId()).isEqualTo("TEST-1234");
        assertThat(result.getAmount()).isEqualByComparingTo("499.00");
        verify(salesRepository, times(1)).save(any(AmazonSale.class));
    }

    @Test
    @DisplayName("Should retrieve sale by ID")
    void shouldGetSaleById() {
        AmazonSale sale = new AmazonSale();
        sale.setIndexId(50L);
        sale.setOrderId("ORDER-50");
        sale.setStatus("Delivered");

        when(salesRepository.findById(50L)).thenReturn(Optional.of(sale));

        SalesResponseDto response = salesService.getSaleById(50L);
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo("ORDER-50");
    }

    @Test
    @DisplayName("Should calculate total revenue and orders")
    void shouldCalculateAnalytics() {
        when(salesRepository.getTotalRevenue()).thenReturn(new BigDecimal("150000.00"));
        when(salesRepository.getTotalOrders()).thenReturn(350L);
        when(salesRepository.getAverageOrderValue()).thenReturn(new BigDecimal("428.57"));

        assertThat(analyticsService.getTotalRevenue()).isEqualByComparingTo("150000.00");
        assertThat(analyticsService.getTotalOrders()).isEqualTo(350L);
        assertThat(analyticsService.getAverageOrderValue()).isEqualByComparingTo("428.57");
    }

    @Test
    @DisplayName("Should generate valid CSV output")
    void shouldExportCsv() {
        AmazonSale sale = new AmazonSale();
        sale.setIndexId(1L);
        sale.setOrderId("ORD-CSV-1");
        sale.setDate(LocalDate.of(2026, 4, 30));
        sale.setStatus("Shipped");
        sale.setSku("SKU-CSV-1");
        sale.setCategory("Western Dress");
        sale.setAmount(new BigDecimal("999.00"));
        sale.setQty(1);

        byte[] csv = CsvExportUtil.exportSalesToCsv(List.of(sale));
        String csvString = new String(csv);

        assertThat(csvString).contains("ORD-CSV-1");
        assertThat(csvString).contains("Western Dress");
        assertThat(csvString).contains("999.00");
    }
}
