import axiosInstance from './axiosInstance';

const BASE_URL = '/api/v1/dashboard';

export const dashboardApi = {
  // Summary KPIs (total records, orders, revenue, avg order value, etc.)
  getSummary: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/summary`);
    return res.data;
  },

  // Total sales revenue
  getTotalRevenue: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/total-revenue`);
    return res.data.totalRevenue;
  },

  // Total distinct order count
  getTotalOrders: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/total-orders`);
    return res.data.totalOrders;
  },

  // Total units sold
  getTotalUnits: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/total-units`);
    return res.data.totalUnits;
  },

  // Average order value
  getAverageOrderValue: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/average-order-value`);
    return res.data.averageOrderValue;
  },

  // Daily/recent sales trend overview
  getSalesOverview: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/sales-overview`);
    return res.data;
  },
};

export default dashboardApi;
