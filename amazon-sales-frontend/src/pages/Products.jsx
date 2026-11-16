import React, { useEffect, useState } from 'react';
import {
  Package,
  TrendingUp,
  Tag,
  Layers,
  Search,
  ExternalLink,
  RefreshCw,
  ShoppingBag,
  Award,
  Filter
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { analyticsApi } from '../api/analyticsApi';
import { formatCurrency, formatNumber } from '../utils/format';
import { useToast } from '../context/ToastContext';
import LoadingSpinner from '../components/LoadingSpinner';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Cell
} from 'recharts';

export default function Products() {
  const navigate = useNavigate();
  const { error } = useToast();
  const [loading, setLoading] = useState(true);
  const [topProducts, setTopProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [limit, setLimit] = useState(15);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');

  const loadProductData = async () => {
    try {
      setLoading(true);
      const [prods, cats] = await Promise.all([
        analyticsApi.getTopProducts(limit),
        analyticsApi.getCategorySales()
      ]);
      setTopProducts(prods || []);
      setCategories(cats || []);
    } catch (err) {
      error(err.message || 'Failed to load product analytics', 'Error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProductData();
  }, [limit]);

  const filteredProducts = topProducts.filter((p) => {
    const matchesSearch =
      !searchTerm ||
      (p.sku && p.sku.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (p.style && p.style.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (p.category && p.category.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesCategory = !selectedCategory || p.category === selectedCategory;

    return matchesSearch && matchesCategory;
  });

  const totalUnits = topProducts.reduce((sum, p) => sum + (Number(p.unitsSold) || 0), 0);
  const totalRevenue = topProducts.reduce((sum, p) => sum + (Number(p.totalRevenue) || 0), 0);

  if (loading) {
    return <LoadingSpinner message="Loading SKU and inventory performance..." fullHeight />;
  }

  return (
    <div className="products-page-container">
      {/* Header */}
      <div className="page-header-actions" style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--color-text-main)' }}>
            Products & SKU Performance
          </h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--color-text-muted)', marginTop: '4px' }}>
            Identify top-performing SKUs, product styles, revenue leaders, and inventory velocity.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            type="button"
            className="btn-secondary"
            onClick={loadProductData}
            style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
          >
            <RefreshCw size={15} />
            <span>Refresh</span>
          </button>
        </div>
      </div>

      {/* Highlights */}
      <div className="stats-grid" style={{ marginBottom: '24px' }}>
        <div className="stat-card stat-blue">
          <div className="stat-card-header">
            <span className="stat-card-title">Top SKU Leader</span>
            <div className="stat-card-icon-bubble"><Award size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value" style={{ fontSize: '1.25rem' }}>
              {topProducts[0]?.sku || 'N/A'}
            </div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">
                {topProducts[0] ? `${formatNumber(topProducts[0].unitsSold)} units • ${formatCurrency(topProducts[0].totalRevenue)}` : ''}
              </span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-amber">
          <div className="stat-card-header">
            <span className="stat-card-title">Total Units (Top SKUs)</span>
            <div className="stat-card-icon-bubble"><ShoppingBag size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">{formatNumber(totalUnits)}</div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Sold across top ranking SKUs</span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-emerald">
          <div className="stat-card-header">
            <span className="stat-card-title">Top SKU Gross Revenue</span>
            <div className="stat-card-icon-bubble"><TrendingUp size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">{formatCurrency(totalRevenue)}</div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Aggregated SKU revenue</span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-purple">
          <div className="stat-card-header">
            <span className="stat-card-title">Unique Categories</span>
            <div className="stat-card-icon-bubble"><Layers size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">{categories.length}</div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Product classifications</span>
            </div>
          </div>
        </div>
      </div>

      {/* Top SKUs Revenue Chart */}
      <div className="card" style={{ marginBottom: '24px' }}>
        <div className="card-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <TrendingUp size={18} color="#2563eb" />
            Top Revenue Generating SKUs
          </h3>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Show:</span>
            <select
              value={limit}
              onChange={(e) => setLimit(Number(e.target.value))}
              style={{ padding: '4px 8px', borderRadius: '4px', fontSize: '0.8rem', border: '1px solid var(--color-border)' }}
            >
              <option value={10}>Top 10 SKUs</option>
              <option value={15}>Top 15 SKUs</option>
              <option value={25}>Top 25 SKUs</option>
            </select>
          </div>
        </div>
        <div className="card-body" style={{ height: '340px' }}>
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={topProducts} margin={{ top: 10, right: 20, left: 10, bottom: 40 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
              <XAxis dataKey="sku" stroke="#94A3B8" fontSize={10} angle={-30} textAnchor="end" interval={0} />
              <YAxis stroke="#94A3B8" fontSize={11} tickFormatter={(v) => `₹${(v / 1000).toFixed(0)}k`} />
              <Tooltip formatter={(val) => [formatCurrency(val), 'Revenue']} />
              <Bar dataKey="totalRevenue" fill="#2563eb" radius={[4, 4, 0, 0]}>
                {topProducts.map((_, index) => (
                  <Cell key={`bar-${index}`} fill={index === 0 ? '#ff9900' : '#2563eb'} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Products Table with Search & Filter */}
      <div className="card">
        <div className="card-header" style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 className="card-title">Top SKU Catalog Matrix</h3>

          <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
            <div style={{ position: 'relative', width: '220px' }}>
              <input
                type="text"
                placeholder="Search SKU, Style..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                style={{
                  width: '100%',
                  padding: '6px 10px 6px 30px',
                  borderRadius: 'var(--radius-md)',
                  border: '1px solid var(--color-border)',
                  fontSize: '0.85rem'
                }}
              />
              <Search size={14} style={{ position: 'absolute', left: '10px', top: '9px', color: 'var(--color-text-muted)' }} />
            </div>

            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              style={{
                padding: '6px 12px',
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--color-border)',
                fontSize: '0.85rem',
                backgroundColor: 'white'
              }}
            >
              <option value="">All Categories</option>
              {categories.map((c) => (
                <option key={c.category} value={c.category}>
                  {c.category}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th style={{ width: '60px' }}>Rank</th>
                <th>SKU Identifier</th>
                <th>Style</th>
                <th>Category</th>
                <th style={{ textAlign: 'right' }}>Units Sold</th>
                <th style={{ textAlign: 'right' }}>Total Sales Amount</th>
                <th style={{ textAlign: 'center', width: '100px' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredProducts.length > 0 ? (
                filteredProducts.map((p, idx) => (
                  <tr key={p.sku || idx}>
                    <td style={{ fontWeight: 700, color: idx < 3 ? '#ff9900' : 'var(--color-text-muted)' }}>
                      #{idx + 1}
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--color-text-main)' }}>
                      <code>{p.sku}</code>
                    </td>
                    <td>{p.style || '—'}</td>
                    <td>
                      <span className="badge" style={{ backgroundColor: '#f1f5f9', color: '#334155' }}>
                        {p.category || 'General'}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 600 }}>
                      {formatNumber(p.unitsSold)}
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: '#059669' }}>
                      {formatCurrency(p.totalRevenue)}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <button
                        type="button"
                        className="btn-secondary"
                        style={{ padding: '4px 8px', fontSize: '0.75rem', display: 'inline-flex', alignItems: 'center', gap: '4px' }}
                        onClick={() => navigate(`/sales?keyword=${encodeURIComponent(p.sku)}`)}
                        title="View all orders for this SKU"
                      >
                        <span>View Orders</span>
                        <ExternalLink size={12} />
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="7" style={{ textAlign: 'center', padding: '30px', color: 'var(--color-text-muted)' }}>
                    No products matched your search criteria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
