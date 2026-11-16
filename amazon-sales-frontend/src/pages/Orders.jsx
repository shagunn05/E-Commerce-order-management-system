import React, { useEffect, useState } from 'react';
import {
  ShoppingCart,
  Truck,
  Package,
  CheckCircle2,
  Clock,
  XCircle,
  Search,
  Filter,
  RefreshCw,
  ExternalLink,
  Building2,
  MapPin,
  Calendar
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { salesApi } from '../api/salesApi';
import { dashboardApi } from '../api/dashboardApi';
import { analyticsApi } from '../api/analyticsApi';
import { formatCurrency, formatDate, formatNumber } from '../utils/format';
import { getStatusBadgeClass } from '../utils/domain';
import { useToast } from '../context/ToastContext';
import LoadingSpinner from '../components/LoadingSpinner';

export default function Orders() {
  const navigate = useNavigate();
  const { error } = useToast();

  const [loading, setLoading] = useState(true);
  const [orders, setOrders] = useState([]);
  const [totalOrders, setTotalOrders] = useState(0);
  const [orderStatusDistribution, setOrderStatusDistribution] = useState([]);
  const [courierStatusDistribution, setCourierStatusDistribution] = useState([]);

  // Filters
  const [statusFilter, setStatusFilter] = useState('');
  const [courierFilter, setCourierFilter] = useState('');
  const [fulfilmentFilter, setFulfilmentFilter] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  const loadOrderData = async () => {
    try {
      setLoading(true);
      const [orderList, stats, courierStats] = await Promise.all([
        salesApi.filter({
          status: statusFilter || undefined,
          courierStatus: courierFilter || undefined,
          fulfilment: fulfilmentFilter || undefined,
        }),
        analyticsApi.getOrderStatus(),
        analyticsApi.getCourierStatus()
      ]);

      setOrders(orderList || []);
      setOrderStatusDistribution(stats || []);
      setCourierStatusDistribution(courierStats || []);
      setTotalOrders(orderList ? orderList.length : 0);
    } catch (err) {
      error(err.message || 'Failed to fetch orders data', 'Error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrderData();
  }, [statusFilter, courierFilter, fulfilmentFilter]);

  const filteredOrders = orders.filter((o) => {
    if (!searchTerm) return true;
    const term = searchTerm.toLowerCase();
    return (
      (o.orderId && o.orderId.toLowerCase().includes(term)) ||
      (o.shipCity && o.shipCity.toLowerCase().includes(term)) ||
      (o.shipState && o.shipState.toLowerCase().includes(term)) ||
      (o.sku && o.sku.toLowerCase().includes(term))
    );
  });

  if (loading) {
    return <LoadingSpinner message="Monitoring live order operations and courier telemetry..." fullHeight />;
  }

  return (
    <div className="orders-page-container">
      {/* Page Header */}
      <div className="page-header-actions" style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--color-text-main)' }}>
            Order Monitoring & Tracking
          </h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--color-text-muted)', marginTop: '4px' }}>
            Real-time tracking of order fulfilment, courier dispatches, delivery statuses, and B2B orders.
          </p>
        </div>
        <button
          type="button"
          className="btn-secondary"
          onClick={loadOrderData}
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <RefreshCw size={15} />
          <span>Refresh Orders</span>
        </button>
      </div>

      {/* KPI Cards */}
      <div className="stats-grid" style={{ marginBottom: '24px' }}>
        <div className="stat-card stat-blue">
          <div className="stat-card-header">
            <span className="stat-card-title">Monitored Orders</span>
            <div className="stat-card-icon-bubble"><ShoppingCart size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">{formatNumber(totalOrders)}</div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Current filter criteria</span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-emerald">
          <div className="stat-card-header">
            <span className="stat-card-title">Delivered Orders</span>
            <div className="stat-card-icon-bubble"><CheckCircle2 size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">
              {orderStatusDistribution.find(s => s.status && s.status.includes('Delivered'))?.count || 0}
            </div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Successfully fulfilled to customer</span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-amber">
          <div className="stat-card-header">
            <span className="stat-card-title">In-Transit / Shipped</span>
            <div className="stat-card-icon-bubble"><Truck size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">
              {courierStatusDistribution.find(s => s.status === 'Shipped')?.count || 0}
            </div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Active carrier deliveries</span>
            </div>
          </div>
        </div>

        <div className="stat-card stat-purple">
          <div className="stat-card-header">
            <span className="stat-card-title">B2B Enterprise Orders</span>
            <div className="stat-card-icon-bubble"><Building2 size={20} className="stat-card-icon" /></div>
          </div>
          <div className="stat-card-body">
            <div className="stat-card-value">
              {orders.filter(o => o.b2b === true).length}
            </div>
            <div className="stat-card-footer">
              <span className="stat-card-subtitle">Wholesale & business transactions</span>
            </div>
          </div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="card" style={{ marginBottom: '24px', padding: '16px' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ position: 'relative', flex: '1', minWidth: '240px' }}>
            <input
              type="text"
              placeholder="Search Order ID, City, State, SKU..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{
                width: '100%',
                padding: '8px 12px 8px 34px',
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--color-border)',
                fontSize: '0.875rem'
              }}
            />
            <Search size={16} style={{ position: 'absolute', left: '10px', top: '10px', color: 'var(--color-text-muted)' }} />
          </div>

          <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
            {/* Status filter */}
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              style={{ padding: '8px 12px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-border)', fontSize: '0.85rem' }}
            >
              <option value="">All Order Statuses</option>
              <option value="Shipped - Delivered to Buyer">Delivered to Buyer</option>
              <option value="Shipped">Shipped</option>
              <option value="Cancelled">Cancelled</option>
              <option value="Pending">Pending</option>
            </select>

            {/* Courier status filter */}
            <select
              value={courierFilter}
              onChange={(e) => setCourierFilter(e.target.value)}
              style={{ padding: '8px 12px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-border)', fontSize: '0.85rem' }}
            >
              <option value="">All Courier Statuses</option>
              <option value="Shipped">Courier Shipped</option>
              <option value="Unshipped">Courier Unshipped</option>
              <option value="Cancelled">Courier Cancelled</option>
            </select>

            {/* Fulfilment */}
            <select
              value={fulfilmentFilter}
              onChange={(e) => setFulfilmentFilter(e.target.value)}
              style={{ padding: '8px 12px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-border)', fontSize: '0.85rem' }}
            >
              <option value="">All Fulfilment Channels</option>
              <option value="Amazon">Amazon FBA / Easy Ship</option>
              <option value="Merchant">Merchant Fulfilled</option>
            </select>

            {(statusFilter || courierFilter || fulfilmentFilter || searchTerm) && (
              <button
                type="button"
                className="btn-secondary"
                onClick={() => {
                  setStatusFilter('');
                  setCourierFilter('');
                  setFulfilmentFilter('');
                  setSearchTerm('');
                }}
                style={{ padding: '8px 12px', fontSize: '0.85rem' }}
              >
                Reset
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Orders Table */}
      <div className="card">
        <div className="card-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 className="card-title">Active Orders Feed</h3>
          <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
            Showing {filteredOrders.length} records
          </span>
        </div>
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Order Date</th>
                <th>Status</th>
                <th>Fulfilment / Carrier</th>
                <th>Product SKU</th>
                <th>Destination</th>
                <th style={{ textAlign: 'right' }}>Qty</th>
                <th style={{ textAlign: 'right' }}>Total Amount</th>
                <th style={{ textAlign: 'center' }}>Type</th>
                <th style={{ textAlign: 'center' }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.length > 0 ? (
                filteredOrders.map((order) => (
                  <tr key={order.indexId || order.orderId}>
                    <td style={{ fontWeight: 600 }}>
                      <code style={{ fontSize: '0.85rem' }}>{order.orderId}</code>
                    </td>
                    <td style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)' }}>
                      {formatDate(order.date)}
                    </td>
                    <td>
                      <span className={`status-badge ${getStatusBadgeClass(order.status)}`}>
                        {order.status || 'Pending'}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '2px' }}>
                        <span style={{ fontWeight: 500, fontSize: '0.85rem' }}>{order.fulfilment || 'Merchant'}</span>
                        <span style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>
                          {order.fulfilledBy || 'Standard'} • {order.courierStatus || 'Pending'}
                        </span>
                      </div>
                    </td>
                    <td>
                      <div style={{ display: 'flex', flexDirection: 'column' }}>
                        <span style={{ fontWeight: 500 }}>{order.sku}</span>
                        <span style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>
                          Size: {order.size || '—'} • {order.category}
                        </span>
                      </div>
                    </td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '0.85rem' }}>
                        <MapPin size={13} color="var(--color-text-muted)" />
                        <span>{order.shipCity || '—'}, {order.shipState || '—'}</span>
                      </div>
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 600 }}>{order.qty}</td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: '#059669' }}>
                      {formatCurrency(order.amount)}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      {order.b2b ? (
                        <span className="badge" style={{ backgroundColor: '#fef3c7', color: '#92400e', fontWeight: 600 }}>
                          B2B
                        </span>
                      ) : (
                        <span className="badge" style={{ backgroundColor: '#f1f5f9', color: '#64748b' }}>
                          B2C
                        </span>
                      )}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <button
                        type="button"
                        className="btn-secondary"
                        style={{ padding: '4px 8px', fontSize: '0.75rem' }}
                        onClick={() => navigate(`/sales?keyword=${encodeURIComponent(order.orderId)}`)}
                        title="Manage Order"
                      >
                        Manage
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="10" style={{ textAlign: 'center', padding: '36px', color: 'var(--color-text-muted)' }}>
                    No orders matching the specified filter criteria.
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
