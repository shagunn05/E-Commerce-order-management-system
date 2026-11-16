import { useState, useEffect, useCallback } from 'react';
import { salesApi } from '../api/salesApi';
import { useToast } from '../context/ToastContext';

export function useSales(initialFilter = {}) {
  const { success, error } = useToast();
  const [sales, setSales] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState(initialFilter);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    totalPages: 0,
    totalElements: 0,
    sortBy: 'index_id',
    sortDirection: 'DESC'
  });

  const fetchSales = useCallback(async () => {
    try {
      setLoading(true);
      const data = await salesApi.getPaginated({
        ...filter,
        page: pagination.page,
        size: pagination.size,
        sortBy: pagination.sortBy,
        sortDirection: pagination.sortDirection
      });

      setSales(data.content || []);
      setPagination(prev => ({
        ...prev,
        totalPages: data.totalPages,
        totalElements: data.totalElements
      }));
    } catch (err) {
      error(err.message || 'Failed to fetch sales records', 'Error');
    } finally {
      setLoading(false);
    }
  }, [filter, pagination.page, pagination.size, pagination.sortBy, pagination.sortDirection]);

  useEffect(() => {
    fetchSales();
  }, [fetchSales]);

  const createSale = async (saleData) => {
    try {
      const created = await salesApi.create(saleData);
      success('Sale record created successfully', 'Created');
      fetchSales();
      return created;
    } catch (err) {
      error(err.message || 'Failed to create record', 'Creation Error');
      throw err;
    }
  };

  const updateSale = async (id, saleData) => {
    try {
      const updated = await salesApi.update(id, saleData);
      success('Sale record updated successfully', 'Updated');
      fetchSales();
      return updated;
    } catch (err) {
      error(err.message || 'Failed to update record', 'Update Error');
      throw err;
    }
  };

  const deleteSale = async (id) => {
    try {
      await salesApi.delete(id);
      success('Sale record deleted successfully', 'Deleted');
      fetchSales();
    } catch (err) {
      error(err.message || 'Failed to delete record', 'Delete Error');
      throw err;
    }
  };

  return {
    sales,
    loading,
    filter,
    setFilter,
    pagination,
    setPagination,
    refresh: fetchSales,
    createSale,
    updateSale,
    deleteSale
  };
}

export default useSales;
