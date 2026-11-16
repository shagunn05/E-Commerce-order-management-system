import axiosInstance from './axiosInstance';

const BASE_URL = '/api/v1/analytics';

export const analyticsApi = {
  // Category-wise revenue, units, and order breakdown
  getCategorySales: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/category-sales`);
    return res.data;
  },

  // Top-selling SKUs and styles
  getTopProducts: async (limit = 10) => {
    const res = await axiosInstance.get(`${BASE_URL}/top-products`, {
      params: { limit },
    });
    return res.data;
  },

  // State-wise geographical revenue
  getSalesByState: async (limit = 10) => {
    const res = await axiosInstance.get(`${BASE_URL}/sales-by-state`, {
      params: { limit },
    });
    return res.data;
  },

  // City-wise geographical revenue
  getSalesByCity: async (limit = 10) => {
    const res = await axiosInstance.get(`${BASE_URL}/sales-by-city`, {
      params: { limit },
    });
    return res.data;
  },

  // Size distribution (XS, S, M, L, XL, XXL, etc.)
  getSalesBySize: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/sales-by-size`);
    return res.data;
  },

  // Sales channel breakdown (Amazon.in, Non-Amazon)
  getSalesByChannel: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/sales-by-channel`);
    return res.data;
  },

  // Fulfilment performance (Merchant vs Amazon)
  getFulfilmentAnalysis: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/fulfilment-analysis`);
    return res.data;
  },

  // Order status distribution (Shipped, Delivered, Cancelled)
  getOrderStatus: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/order-status`);
    return res.data;
  },

  // Courier status distribution (Shipped, Unshipped, Cancelled)
  getCourierStatus: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/courier-status`);
    return res.data;
  },

  // Monthly revenue trend
  getMonthlyRevenue: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/monthly-revenue`);
    return res.data;
  },

  // Daily sales metrics
  getDailySales: async (limit = 30) => {
    const res = await axiosInstance.get(`${BASE_URL}/daily-sales`, {
      params: { limit },
    });
    return res.data;
  },

  // Complete analytics dashboard overview
  getOverview: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/overview`);
    return res.data;
  },
};

export default analyticsApi;
