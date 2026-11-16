import { useState, useEffect, useCallback } from 'react';
import { analyticsApi } from '../api/analyticsApi';
import { useToast } from '../context/ToastContext';

export function useAnalytics(geoLimit = 8) {
  const { error } = useToast();
  const [data, setData] = useState({
    categories: [],
    states: [],
    cities: [],
    sizes: [],
    channels: [],
    fulfilments: [],
    orderStatuses: [],
    courierStatuses: [],
    monthlyRevenue: []
  });
  const [loading, setLoading] = useState(true);

  const fetchAnalytics = useCallback(async () => {
    try {
      setLoading(true);
      const [
        categories,
        states,
        cities,
        sizes,
        channels,
        fulfilments,
        orderStatuses,
        courierStatuses,
        monthlyRevenue
      ] = await Promise.all([
        analyticsApi.getCategorySales(),
        analyticsApi.getSalesByState(geoLimit),
        analyticsApi.getSalesByCity(geoLimit),
        analyticsApi.getSalesBySize(),
        analyticsApi.getSalesByChannel(),
        analyticsApi.getFulfilmentAnalysis(),
        analyticsApi.getOrderStatus(),
        analyticsApi.getCourierStatus(),
        analyticsApi.getMonthlyRevenue()
      ]);

      setData({
        categories: categories || [],
        states: states || [],
        cities: cities || [],
        sizes: sizes || [],
        channels: channels || [],
        fulfilments: fulfilments || [],
        orderStatuses: orderStatuses || [],
        courierStatuses: courierStatuses || [],
        monthlyRevenue: monthlyRevenue || []
      });
    } catch (err) {
      error(err.message || 'Failed to load analytics', 'Analytics Error');
    } finally {
      setLoading(false);
    }
  }, [geoLimit]);

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  return {
    ...data,
    loading,
    refresh: fetchAnalytics
  };
}

export default useAnalytics;
