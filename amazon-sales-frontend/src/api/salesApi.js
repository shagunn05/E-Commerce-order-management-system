import axiosInstance from './axiosInstance';

const BASE_URL = '/api/v1/sales';

export const salesApi = {
  // Get all sales
  getAll: async () => {
    const res = await axiosInstance.get(BASE_URL);
    return res.data;
  },

  // Get sale by ID
  getById: async (id) => {
    const res = await axiosInstance.get(`${BASE_URL}/${id}`);
    return res.data;
  },

  // Create new sale record
  create: async (saleData) => {
    const res = await axiosInstance.post(BASE_URL, saleData);
    return res.data;
  },

  // Update existing sale record
  update: async (id, saleData) => {
    const res = await axiosInstance.put(`${BASE_URL}/${id}`, saleData);
    return res.data;
  },

  // Delete sale record
  delete: async (id) => {
    const res = await axiosInstance.delete(`${BASE_URL}/${id}`);
    return res.data;
  },

  // Search sales across multiple fields
  search: async (keyword) => {
    const res = await axiosInstance.get(`${BASE_URL}/search`, {
      params: { keyword },
    });
    return res.data;
  },

  // Multi-column filtered sales
  filter: async (filterParams = {}) => {
    const res = await axiosInstance.get(`${BASE_URL}/filter`, {
      params: filterParams,
    });
    return res.data;
  },

  // Server-side paginated and sorted sales
  getPaginated: async (params = {}) => {
    const res = await axiosInstance.get(`${BASE_URL}/paginated`, {
      params: {
        page: params.page ?? 0,
        size: params.size ?? 20,
        sortBy: params.sortBy ?? 'index_id',
        sortDirection: params.sortDirection ?? 'DESC',
        keyword: params.keyword || undefined,
        category: params.category || undefined,
        status: params.status || undefined,
        fulfilment: params.fulfilment || undefined,
        shipCity: params.shipCity || undefined,
        shipState: params.shipState || undefined,
        minAmount: params.minAmount || undefined,
        maxAmount: params.maxAmount || undefined,
        startDate: params.startDate || undefined,
        endDate: params.endDate || undefined,
        b2b: params.b2b !== undefined ? params.b2b : undefined,
      },
    });
    return res.data;
  },
};

export default salesApi;
