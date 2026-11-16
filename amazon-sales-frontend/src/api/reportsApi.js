import axiosInstance from './axiosInstance';

const BASE_URL = '/api/v1/reports';

export const reportsApi = {
  // Trigger CSV file download
  downloadSalesCsv: async (filterParams = {}) => {
    const res = await axiosInstance.get(`${BASE_URL}/sales/csv`, {
      params: filterParams,
      responseType: 'blob',
    });

    // Create a client-side download link
    const blob = new Blob([res.data], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `amazon_sales_report_${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },

  // Sales summary report
  getSalesSummary: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/sales/summary`);
    return res.data;
  },

  // Category performance report
  getCategoryReport: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/category`);
    return res.data;
  },

  // Location intelligence report
  getLocationReport: async () => {
    const res = await axiosInstance.get(`${BASE_URL}/location`);
    return res.data;
  },
};

export default reportsApi;
