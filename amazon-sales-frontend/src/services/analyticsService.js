import api from './apiClient';
import { endpoints } from './endpoints';
import { useMockData, withMockLatency } from './config';
import productsService from './productsService';
import ordersService from './ordersService';
import customersService from './customersService';
import { buildOverview } from '../utils/analytics';

export const analyticsService = {
  async getOverview() {
    // Aggregates overview data from services
    const [products, orders, customers] = await Promise.all([
      productsService.getAll(),
      ordersService.getAll(),
      customersService.getAll(),
    ]);
    const overview = buildOverview(products, orders, customers);
    return withMockLatency(overview);
  },

  async getOrderStatusCounts() {
    if (useMockData()) {
      const orders = await ordersService.getAll();
      const counts = {};
      orders.forEach((o) => {
        counts[o.status] = (counts[o.status] || 0) + 1;
      });
      return withMockLatency(counts);
    }
    const response = await api.get(endpoints.analytics.orderStatusCounts);
    return response.data;
  },

  async getRevenueByStatus() {
    if (useMockData()) {
      const orders = await ordersService.getAll();
      const revenue = {};
      orders.forEach((o) => {
        revenue[o.status] = Math.round(((revenue[o.status] || 0) + o.totalAmount) * 100) / 100;
      });
      return withMockLatency(revenue);
    }
    const response = await api.get(endpoints.analytics.revenueByStatus);
    return response.data;
  },

  async getValuationStats() {
    if (useMockData()) {
      const orders = await ordersService.getAll();
      const amounts = orders.map((o) => o.totalAmount);
      const sum = Math.round(amounts.reduce((a, b) => a + b, 0) * 100) / 100;
      const stats = {
        count: amounts.length,
        sum,
        min: amounts.length ? Math.min(...amounts) : 0,
        max: amounts.length ? Math.max(...amounts) : 0,
        average: amounts.length ? Math.round((sum / amounts.length) * 100) / 100 : 0,
      };
      return withMockLatency(stats);
    }
    const response = await api.get(endpoints.analytics.valuationStats);
    return response.data;
  },

  async getInventoryValuation() {
    if (useMockData()) {
      const products = await productsService.getAll();
      const catMap = {};
      products.forEach((p) => {
        catMap[p.category] =
          Math.round(((catMap[p.category] || 0) + p.price * p.stockQuantity) * 100) / 100;
      });
      return withMockLatency(catMap);
    }
    const response = await api.get(endpoints.analytics.inventoryValuation);
    return response.data;
  },

  async getTopCustomers(topN = 5) {
    if (useMockData()) {
      const orders = await ordersService.getAll();
      const active = orders.filter((o) => o.status !== 'CANCELLED');
      const spendMap = {};
      active.forEach((o) => {
        spendMap[o.customerName] =
          Math.round(((spendMap[o.customerName] || 0) + o.totalAmount) * 100) / 100;
      });
      const sorted = Object.fromEntries(
        Object.entries(spendMap)
          .sort(([, a], [, b]) => b - a)
          .slice(0, topN),
      );
      return withMockLatency(sorted);
    }
    const response = await api.get(endpoints.analytics.topCustomers, {
      params: { topN },
    });
    return response.data;
  },
};

export default analyticsService;
