import { useState, useEffect, useCallback } from 'react';
import { dashboardApi } from '../api/dashboardApi';
import { analyticsApi } from '../api/analyticsApi';
import { useToast } from '../context/ToastContext';

export function useDashboard() {
  const { error } = useToast();
  const [stats, setStats] = useState(null);
  const [salesTrend, setSalesTrend] = useState([]);
  const [categorySales, setCategorySales] = useState([]);
  const [fulfilmentData, setFulfilmentData] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchDashboard = useCallback(async () => {
    try {
      setLoading(true);
      const [summary, trend, cats, fulfils] = await Promise.all([
        dashboardApi.getSummary(),
        dashboardApi.getSalesOverview(),
        analyticsApi.getCategorySales(),
        analyticsApi.getFulfilmentAnalysis()
      ]);

      setStats(summary);
      setSalesTrend(trend || []);
      setCategorySales(cats || []);
      setFulfilmentData(fulfils || []);
    } catch (err) {
      error(err.message || 'Failed to load dashboard metrics', 'Dashboard Error');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDashboard();
  }, [fetchDashboard]);

  return {
    stats,
    salesTrend,
    categorySales,
    fulfilmentData,
    loading,
    refresh: fetchDashboard
  };
}

export default useDashboard;
