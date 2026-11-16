package com.amazon.sales.repository;

import com.amazon.sales.dto.*;
import com.amazon.sales.model.AmazonSale;
import com.amazon.sales.model.SalesFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data access abstraction for Amazon Sales entity and analytics.
 */
public interface SalesRepository {

    List<AmazonSale> findAll();

    Optional<AmazonSale> findById(Long id);

    AmazonSale save(AmazonSale sale);

    AmazonSale update(Long id, AmazonSale sale);

    boolean deleteById(Long id);

    boolean existsById(Long id);

    List<AmazonSale> search(String keyword);

    List<AmazonSale> filter(SalesFilter filter);

    PageResponseDto<AmazonSale> findPaginated(SalesFilter filter);

    DashboardStatsDto getDashboardStats();

    BigDecimal getTotalRevenue();

    Long getTotalOrders();

    Long getTotalUnits();

    BigDecimal getAverageOrderValue();

    List<DailySalesDto> getSalesOverview();

    List<CategorySalesDto> getCategorySales();

    List<TopProductDto> getTopProducts(int limit);

    List<LocationSalesDto> getSalesByState(int limit);

    List<LocationSalesDto> getSalesByCity(int limit);

    List<StatusDistributionDto> getSalesBySize();

    List<StatusDistributionDto> getSalesByChannel();

    List<StatusDistributionDto> getFulfilmentAnalysis();

    List<StatusDistributionDto> getOrderStatusDistribution();

    List<StatusDistributionDto> getCourierStatusDistribution();

    List<MonthlyRevenueDto> getMonthlyRevenue();

    List<DailySalesDto> getDailySales(int limit);
}
