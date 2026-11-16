import React, { useEffect, useState } from 'react';
import {
  BarChart3,
  TrendingUp,
  MapPin,
  PieChart as PieIcon,
  Truck,
  Package,
  Layers,
  RefreshCw,
  CheckCircle2,
  Calendar,
  DollarSign
} from 'lucide-react';
import { analyticsApi } from '../api/analyticsApi';
import { formatCurrency, formatNumber, formatPercent } from '../utils/format';
import { useToast } from '../context/ToastContext';
import LoadingSpinner from '../components/LoadingSpinner';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from 'recharts';

const COLORS = ['#2563eb', '#ff9900', '#10b981', '#8b5cf6', '#f59e0b', '#dc2626', '#06b6d4', '#ec4899'];

export default function Analytics() {
  const { error } = useToast();
  const [loading, setLoading] = useState(true);

  const [categorySales, setCategorySales] = useState([]);
  const [stateSales, setStateSales] = useState([]);
  const [citySales, setCitySales] = useState([]);
  const [sizeDistribution, setSizeDistribution] = useState([]);
  const [channelData, setChannelData] = useState([]);
  const [fulfilmentData, setFulfilmentData] = useState([]);
  const [orderStatusData, setOrderStatusData] = useState([]);
  const [courierStatusData, setCourierStatusData] = useState([]);
  const [monthlyRevenue, setMonthlyRevenue] = useState([]);

  const [geoLimit, setGeoLimit] = useState(8);

  const loadAnalytics = async () => {
    try {
      setLoading(true);
      const [
        cats,
        states,
        cities,
        sizes,
        channels,
        fulfils,
        orderStats,
        courierStats,
        months
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

      setCategorySales(cats || []);
      setStateSales(states || []);
      setCitySales(cities || []);
      setSizeDistribution(sizes || []);
      setChannelData(channels || []);
      setFulfilmentData(fulfils || []);
      setOrderStatusData(orderStats || []);
      setCourierStatusData(courierStats || []);
      setMonthlyRevenue(months || []);
    } catch (err) {
      error(err.message || 'Failed to load analytics', 'Analytics Error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnalytics();
  }, [geoLimit]);

  if (loading) {
    return <LoadingSpinner message="Calculating multi-dimensional sales analytics..." fullHeight />;
  }

  const totalCategoryRev = categorySales.reduce((acc, c) => acc + (Number(c.totalRevenue) || 0), 0);

  return (
    <div className="analytics-page-container">
      {/* Header bar */}
      <div className="page-header-actions" style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--color-text-main)' }}>
            Sales Analytics & Intelligence
          </h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--color-text-muted)', marginTop: '4px' }}>
            Multi-dimensional performance analysis across categories, geography, fulfilment, and size metrics.
          </p>
        </div>
        <button
          type="button"
          className="btn-secondary"
          onClick={loadAnalytics}
          title="Refresh metrics"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <RefreshCw size={15} />
          <span>Refresh Analytics</span>
        </button>
      </div>

      {/* KPI highlight row */}
      <div className="stats-grid" style={{ marginBottom: '24px' }}>
        <div className="stat-card stat-blue">
          <div className="stat-card-header">
            <span className="stat-card-title">Product Categories</span>
            <div className="stat-card-icon-bubble"><Layers size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">{categorySales.length} Categories</div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Active product verticals</span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-amber">
          <div className="stat-card-header">
            <span className="stat-card-title">Top Channel</span>
            <div className="stat-card-icon-bubble"><Package size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">
              {channelData.length > 0 ? channelData[0].status : 'Amazon.in'}
            </div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">
                {channelData.length > 0 ? `${formatNumber(channelData[0].count)} orders (${channelData[0].percentage}%)` : 'Primary marketplace'}
              </span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-emerald">
          <div className="stat-card-header">
            <span className="stat-card-title">Top Fulfilment Model</span>
            <div className="stat-card-icon-bubble"><Truck size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">
              {fulfilmentData.length > 0 ? fulfilmentData[0].status : 'Amazon FBA'}
            </div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">
                {fulfilmentData.length > 0 ? `${fulfilmentData[0].percentage}% of total shipments` : 'High speed delivery'}
              </span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-purple">
          <div className="stat-card-header">
            <span className="stat-card-title">Analyzed Geographies</span>
            <div className="stat-card-icon-bubble"><MapPin size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">{stateSales.length} States</div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Across India distribution</span>
            </div>
          </div>
        </div>
      </div>

      {/* Row 1: Monthly Revenue & Category Sales */}
      <div className="grid grid-2" style={{ marginBottom: '24px' }}>
        <div className="card">
          <div className="card-header">
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <TrendingUp size={18} color="#2563eb" />
              Monthly Revenue Trajectory
            </h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Historical Performance</span>
          </div>
          <div className="card-body" style={{ height: '320px' }}>
            {monthlyRevenue.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={monthlyRevenue} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                  <defs>
                    <linearGradient id="monthRevGrad" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#ff9900" stopOpacity={0.4} />
                      <stop offset="95%" stopColor="#ff9900" stopOpacity={0.0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
                  <XAxis dataKey="month" stroke="#94A3B8" fontSize={12} tickLine={false} />
                  <YAxis stroke="#94A3B8" fontSize={12} tickLine={false} tickFormatter={(val) => `₹${(val/1000).toFixed(0)}k`} />
                  <Tooltip formatter={(value) => [formatCurrency(value), 'Revenue']} />
                  <Area type="monotone" dataKey="revenue" stroke="#ff9900" strokeWidth={2.5} fill="url(#monthRevGrad)" />
                </AreaChart>
              </ResponsiveContainer>
            ) : (
              <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--color-text-muted)' }}>No monthly revenue records available.</div>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <BarChart3 size={18} color="#ff9900" />
              Category Revenue Distribution
            </h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Revenue by Vertical</span>
          </div>
          <div className="card-body" style={{ height: '320px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={categorySales} margin={{ top: 10, right: 20, left: 10, bottom: 25 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
                <XAxis dataKey="category" stroke="#94A3B8" fontSize={11} angle={-25} textAnchor="end" />
                <YAxis stroke="#94A3B8" fontSize={12} tickLine={false} tickFormatter={(val) => `₹${(val/1000).toFixed(0)}k`} />
                <Tooltip formatter={(val) => [formatCurrency(val), 'Revenue']} />
                <Bar dataKey="totalRevenue" fill="#2563eb" radius={[4, 4, 0, 0]}>
                  {categorySales.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Row 2: Geographical Analytics (State & City) */}
      <div className="grid grid-2" style={{ marginBottom: '24px' }}>
        <div className="card">
          <div className="card-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <MapPin size={18} color="#059669" />
              Top States by Sales Volume
            </h3>
            <select
              value={geoLimit}
              onChange={(e) => setGeoLimit(Number(e.target.value))}
              style={{ padding: '4px 8px', borderRadius: '4px', fontSize: '0.8rem', border: '1px solid var(--color-border)' }}
            >
              <option value={5}>Top 5 States</option>
              <option value={8}>Top 8 States</option>
              <option value={12}>Top 12 States</option>
            </select>
          </div>
          <div className="card-body" style={{ height: '320px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart layout="vertical" data={stateSales} margin={{ top: 5, right: 30, left: 40, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#E2E8F0" />
                <XAxis type="number" stroke="#94A3B8" fontSize={11} tickFormatter={(val) => `₹${(val/1000).toFixed(0)}k`} />
                <YAxis type="category" dataKey="location" stroke="#94A3B8" fontSize={11} />
                <Tooltip formatter={(val) => [formatCurrency(val), 'Sales']} />
                <Bar dataKey="totalAmount" fill="#059669" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <MapPin size={18} color="#8b5cf6" />
              Top Cities by Order Value
            </h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Metro vs Tier 2/3</span>
          </div>
          <div className="card-body" style={{ height: '320px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart layout="vertical" data={citySales} margin={{ top: 5, right: 30, left: 50, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#E2E8F0" />
                <XAxis type="number" stroke="#94A3B8" fontSize={11} tickFormatter={(val) => `₹${(val/1000).toFixed(0)}k`} />
                <YAxis type="category" dataKey="location" stroke="#94A3B8" fontSize={11} />
                <Tooltip formatter={(val) => [formatCurrency(val), 'Sales']} />
                <Bar dataKey="totalAmount" fill="#8b5cf6" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Row 3: Size Distribution, Fulfilment & Status */}
      <div className="grid grid-3" style={{ marginBottom: '24px' }}>
        {/* Size Distribution */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Size Breakdown</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Garment Sizing</span>
          </div>
          <div className="card-body" style={{ height: '260px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={sizeDistribution} margin={{ top: 10, right: 10, left: -10, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
                <XAxis dataKey="status" stroke="#94A3B8" fontSize={11} />
                <YAxis stroke="#94A3B8" fontSize={11} />
                <Tooltip />
                <Bar dataKey="count" fill="#ff9900" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Fulfilment Channel */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Fulfilment Distribution</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Amazon vs Merchant</span>
          </div>
          <div className="card-body" style={{ height: '260px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={fulfilmentData}
                  dataKey="count"
                  nameKey="status"
                  cx="50%"
                  cy="50%"
                  outerRadius={75}
                  innerRadius={45}
                  paddingAngle={5}
                >
                  {fulfilmentData.map((entry, idx) => (
                    <Cell key={`fulfil-${idx}`} fill={idx === 0 ? '#2563eb' : '#ff9900'} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend verticalAlign="bottom" height={36} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Courier Status */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Courier Status Ratio</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Logistics Lifecycle</span>
          </div>
          <div className="card-body" style={{ height: '260px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={courierStatusData}
                  dataKey="count"
                  nameKey="status"
                  cx="50%"
                  cy="50%"
                  outerRadius={75}
                  innerRadius={45}
                  paddingAngle={5}
                >
                  {courierStatusData.map((entry, idx) => (
                    <Cell key={`courier-${idx}`} fill={COLORS[idx % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend verticalAlign="bottom" height={36} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Row 4: Detailed Category Breakdown Table */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Comprehensive Category Performance Matrix</h3>
          <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
            Total Catalog Revenue: {formatCurrency(totalCategoryRev)}
          </span>
        </div>
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Category</th>
                <th style={{ textAlign: 'right' }}>Total Units Sold</th>
                <th style={{ textAlign: 'right' }}>Distinct Orders</th>
                <th style={{ textAlign: 'right' }}>Total Revenue</th>
                <th style={{ textAlign: 'right' }}>Avg Unit Price</th>
                <th style={{ textAlign: 'right' }}>% of Revenue</th>
              </tr>
            </thead>
            <tbody>
              {categorySales.map((cat, idx) => {
                const pct = totalCategoryRev > 0 ? ((cat.totalRevenue / totalCategoryRev) * 100).toFixed(1) : '0';
                return (
                  <tr key={cat.category || idx}>
                    <td style={{ fontWeight: 600, color: 'var(--color-text-main)' }}>
                      <span style={{ display: 'inline-block', width: '10px', height: '10px', borderRadius: '50%', backgroundColor: COLORS[idx % COLORS.length], marginRight: '8px' }}></span>
                      {cat.category || 'Unassigned'}
                    </td>
                    <td style={{ textAlign: 'right' }}>{formatNumber(cat.totalUnits)}</td>
                    <td style={{ textAlign: 'right' }}>{formatNumber(cat.orderCount)}</td>
                    <td style={{ textAlign: 'right', fontWeight: 600, color: '#059669' }}>
                      {formatCurrency(cat.totalRevenue)}
                    </td>
                    <td style={{ textAlign: 'right' }}>{formatCurrency(cat.averagePrice)}</td>
                    <td style={{ textAlign: 'right' }}>
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '8px' }}>
                        <span>{pct}%</span>
                        <div style={{ width: '60px', height: '6px', backgroundColor: '#e2e8f0', borderRadius: '3px', overflow: 'hidden' }}>
                          <div style={{ width: `${pct}%`, height: '100%', backgroundColor: COLORS[idx % COLORS.length] }}></div>
                        </div>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
