import React, { useEffect, useState } from 'react';
import {
  DollarSign,
  ShoppingCart,
  Package,
  TrendingUp,
  CheckCircle,
  XCircle,
  Plus,
  Download,
  RefreshCw,
  ShoppingBag,
  ArrowRight,
  Truck,
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import StatCard from '../components/StatCard';
import { dashboardApi } from '../api/dashboardApi';
import { analyticsApi } from '../api/analyticsApi';
import { salesApi } from '../api/salesApi';
import { reportsApi } from '../api/reportsApi';
import { formatCurrency, formatNumber, formatDate } from '../utils/format';
import { getStatusBadgeClass } from '../utils/domain';
import { useToast } from '../context/ToastContext';
import SaleModal from '../components/SaleModal';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts';

const PIE_COLORS = ['#2563eb', '#ff9900', '#10b981', '#f59e0b', '#dc2626', '#8b5cf6'];

export default function Dashboard() {
  const navigate = useNavigate();
  const { success, error, info } = useToast();

  const [stats, setStats] = useState(null);
  const [salesTrend, setSalesTrend] = useState([]);
  const [categorySales, setCategorySales] = useState([]);
  const [fulfilmentData, setFulfilmentData] = useState([]);
  const [recentSales, setRecentSales] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [summaryRes, trendRes, catRes, fulRes, recRes] = await Promise.allSettled([
        dashboardApi.getSummary(),
        dashboardApi.getSalesOverview(),
        analyticsApi.getCategorySales(),
        analyticsApi.getFulfilmentAnalysis(),
        salesApi.getPaginated({ page: 0, size: 8, sortBy: 'index_id', sortDirection: 'DESC' }),
      ]);

      if (summaryRes.status === 'fulfilled') setStats(summaryRes.value);
      if (trendRes.status === 'fulfilled') setSalesTrend(trendRes.value || []);
      if (catRes.status === 'fulfilled') setCategorySales(catRes.value || []);
      if (fulRes.status === 'fulfilled') setFulfilmentData(fulRes.value || []);
      if (recRes.status === 'fulfilled') setRecentSales(recRes.value?.content || []);
    } catch (err) {
      console.error('Error loading dashboard:', err);
      error('Failed to connect to Spring Boot backend. Please verify server is running on port 8080.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const handleCreateSale = async (formData) => {
    try {
      setModalLoading(true);
      const created = await salesApi.create(formData);
      success(`Sale record #${created.indexId} created and synced to PostgreSQL!`, 'Success');
      setIsModalOpen(false);
      fetchDashboardData();
    } catch (err) {
      error(err.message || 'Failed to create record', 'Database Error');
    } finally {
      setModalLoading(false);
    }
  };

  const handleExportCsv = async () => {
    try {
      info('Generating Amazon Sales CSV report...', 'Exporting');
      await reportsApi.downloadSalesCsv();
      success('CSV downloaded successfully.', 'Export Complete');
    } catch (err) {
      error(err.message || 'CSV download failed', 'Export Error');
    }
  };

  return (
    <div className="page-container">
      {/* Header Bar */}
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h1 className="page-title" style={{ fontSize: '1.75rem', fontWeight: 700, color: 'var(--color-text-main)' }}>
            Amazon Sales Executive Overview
          </h1>
          <p className="page-subtitle" style={{ color: 'var(--color-text-muted)', fontSize: '0.9rem' }}>
            Centralized Business Intelligence & Live Order Management (PostgreSQL: ecommerce_db)
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={fetchDashboardData}
            title="Refresh Metrics"
          >
            <RefreshCw size={15} />
            <span>Refresh</span>
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleExportCsv}
            title="Download full CSV"
          >
            <Download size={15} />
            <span>Export CSV</span>
          </button>
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => setIsModalOpen(true)}
          >
            <Plus size={16} />
            <span>Add Sale Record</span>
          </button>
        </div>
      </div>

      {/* KPI Cards Row */}
      <div className="stat-cards-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <StatCard
          title="Total Gross Revenue"
          value={formatCurrency(stats?.totalRevenue ?? 0)}
          subtitle="Non-cancelled order value"
          icon={DollarSign}
          colorScheme="navy"
        />
        <StatCard
          title="Total Orders"
          value={formatNumber(stats?.totalOrders ?? 0)}
          subtitle="Distinct Amazon order IDs"
          icon={ShoppingCart}
          colorScheme="blue"
        />
        <StatCard
          title="Units Sold"
          value={formatNumber(stats?.totalUnits ?? 0)}
          subtitle="Total product items sold"
          icon={Package}
          colorScheme="purple"
        />
        <StatCard
          title="Average Order Value"
          value={formatCurrency(stats?.averageOrderValue ?? 0)}
          subtitle="Revenue / distinct orders"
          icon={TrendingUp}
          colorScheme="emerald"
        />
        <StatCard
          title="Delivery Success"
          value={`${stats?.deliveredRate ?? 0}%`}
          subtitle="Delivered & Shipped orders"
          icon={CheckCircle}
          colorScheme="emerald"
        />
        <StatCard
          title="Cancellation Rate"
          value={`${stats?.cancelledRate ?? 0}%`}
          subtitle="Total cancelled transactions"
          icon={XCircle}
          colorScheme="amber"
        />
      </div>

      {/* Main Charts Row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(460px, 1fr))', gap: '20px', marginBottom: '24px' }}>
        {/* Sales Trend Chart */}
        <div className="card" style={{ padding: '20px', backgroundColor: 'var(--color-bg-surface)', borderRadius: 'var(--radius-lg)', boxShadow: 'var(--shadow-sm)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-text-main)' }}>Daily Sales Revenue Trend</h3>
              <p style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Real-time revenue timeline over dates</p>
            </div>
            <span style={{ fontSize: '0.75rem', padding: '4px 8px', background: '#dbeafe', color: '#1d4ed8', borderRadius: '4px', fontWeight: 600 }}>
              Live Spring JDBC
            </span>
          </div>
          <div style={{ height: 280 }}>
            {salesTrend && salesTrend.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={salesTrend}>
                  <defs>
                    <linearGradient id="revenueGrad" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#2563eb" stopOpacity={0.4} />
                      <stop offset="95%" stopColor="#2563eb" stopOpacity={0.0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                  <XAxis dataKey="date" tick={{ fontSize: 11 }} tickLine={false} />
                  <YAxis tick={{ fontSize: 11 }} tickLine={false} tickFormatter={(v) => `₹${v}`} />
                  <Tooltip
                    formatter={(val) => [formatCurrency(val), 'Revenue']}
                    labelFormatter={(lbl) => `Date: ${lbl}`}
                  />
                  <Area
                    type="monotone"
                    dataKey="revenue"
                    stroke="#2563eb"
                    strokeWidth={2.5}
                    fill="url(#revenueGrad)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            ) : (
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: 'var(--color-text-muted)' }}>
                No trend data available
              </div>
            )}
          </div>
        </div>

        {/* Category Revenue Chart */}
        <div className="card" style={{ padding: '20px', backgroundColor: 'var(--color-bg-surface)', borderRadius: 'var(--radius-lg)', boxShadow: 'var(--shadow-sm)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-text-main)' }}>Revenue by Category</h3>
              <p style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Top categories by total revenue generated</p>
            </div>
            <button
              type="button"
              className="btn btn-secondary"
              style={{ fontSize: '0.75rem', padding: '4px 8px' }}
              onClick={() => navigate('/analytics')}
            >
              Details <ArrowRight size={12} />
            </button>
          </div>
          <div style={{ height: 280 }}>
            {categorySales && categorySales.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={categorySales.slice(0, 7)}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                  <XAxis dataKey="category" tick={{ fontSize: 10 }} tickLine={false} interval={0} />
                  <YAxis tick={{ fontSize: 11 }} tickLine={false} tickFormatter={(v) => `₹${v}`} />
                  <Tooltip formatter={(val) => [formatCurrency(val), 'Revenue']} />
                  <Bar dataKey="totalRevenue" name="Revenue" fill="#1e3a8a" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: 'var(--color-text-muted)' }}>
                No category data available
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Secondary Row: Fulfilment Analysis & Recent Sales */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', gap: '20px' }}>
        {/* Fulfilment Distribution */}
        <div className="card" style={{ padding: '20px', backgroundColor: 'var(--color-bg-surface)', borderRadius: 'var(--radius-lg)', boxShadow: 'var(--shadow-sm)' }}>
          <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-text-main)', marginBottom: '4px' }}>
            Fulfilment Performance
          </h3>
          <p style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', marginBottom: '16px' }}>
            Amazon FBA vs Merchant (Easy Ship) revenue & units
          </p>
          <div style={{ height: 240 }}>
            {fulfilmentData && fulfilmentData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Tooltip formatter={(v) => [formatCurrency(v), 'Revenue']} />
                  <Legend verticalAlign="bottom" iconType="circle" />
                  <Pie
                    data={fulfilmentData}
                    dataKey="revenue"
                    nameKey="name"
                    cx="50%"
                    cy="45%"
                    innerRadius={55}
                    outerRadius={85}
                    paddingAngle={5}
                  >
                    {fulfilmentData.map((entry, index) => (
                      <Cell key={`cell-${entry.name}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                    ))}
                  </Pie>
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: 'var(--color-text-muted)' }}>
                No fulfilment data
              </div>
            )}
          </div>
        </div>

        {/* Recent Transactions Table */}
        <div className="card" style={{ padding: '20px', backgroundColor: 'var(--color-bg-surface)', borderRadius: 'var(--radius-lg)', boxShadow: 'var(--shadow-sm)', gridColumn: 'span 2' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-text-main)' }}>
                Recent Sales Transactions
              </h3>
              <p style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
                Latest records stored in PostgreSQL amazon_sales
              </p>
            </div>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => navigate('/sales')}
            >
              <span>Manage All Sales</span>
              <ArrowRight size={14} />
            </button>
          </div>

          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Date</th>
                  <th>SKU</th>
                  <th>Category</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Fulfilment</th>
                  <th>City</th>
                </tr>
              </thead>
              <tbody>
                {recentSales && recentSales.length > 0 ? (
                  recentSales.map((sale) => (
                    <tr key={sale.indexId}>
                      <td style={{ fontWeight: 600, color: 'var(--color-primary)' }}>{sale.orderId}</td>
                      <td>{formatDate(sale.date)}</td>
                      <td><code style={{ fontSize: '0.8rem' }}>{sale.sku}</code></td>
                      <td>{sale.category}</td>
                      <td style={{ fontWeight: 600 }}>{formatCurrency(sale.amount)}</td>
                      <td>
                        <span className={`status-badge ${getStatusBadgeClass(sale.status)}`}>
                          {sale.status}
                        </span>
                      </td>
                      <td>
                        <span style={{ fontSize: '0.8rem', padding: '2px 8px', borderRadius: '4px', background: sale.fulfilment === 'Amazon' ? '#dbeafe' : '#fef3c7', color: sale.fulfilment === 'Amazon' ? '#1e40af' : '#b45309' }}>
                          {sale.fulfilment}
                        </span>
                      </td>
                      <td style={{ fontSize: '0.85rem' }}>{sale.shipCity || '—'}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={8} style={{ textAlign: 'center', padding: '24px', color: 'var(--color-text-muted)' }}>
                      {loading ? 'Loading sales records from PostgreSQL...' : 'No sales records found.'}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Add Sale Modal */}
      <SaleModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleCreateSale}
        isLoading={modalLoading}
      />
    </div>
  );
}
