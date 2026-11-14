import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  AlertCircle,
  ArrowRight,
  Boxes,
  CheckCircle2,
  Clock,
  DollarSign,
  Eye,
  Package,
  RotateCcw,
  ShoppingCart,
  TrendingUp,
  Truck,
  Users,
  Zap,
} from 'lucide-react';
import StatCard from '../components/StatCard';
import StatusBadge from '../components/StatusBadge';
import TierBadge from '../components/TierBadge';
import LoadingSpinner from '../components/LoadingSpinner';
import Modal from '../components/Modal';
import { OrderStatusPieChart, RevenueTrendChart } from '../components/Charts';
import analyticsService from '../services/analyticsService';
import ordersService from '../services/ordersService';
import { formatCurrency, formatDate, formatDateTime, formatNumber } from '../utils/format';
import { useToast } from '../context/ToastContext';
import { STATUS_TRANSITIONS } from '../utils/domain';

export default function DashboardPage() {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [isUpdatingStatus, setIsUpdatingStatus] = useState(false);
  const { success, error } = useToast();
  const navigate = useNavigate();

  const loadDashboardData = async () => {
    try {
      setIsLoading(true);
      const overview = await analyticsService.getOverview();
      setData(overview);
    } catch (err) {
      error(err.message, 'Failed to load dashboard data');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      setIsUpdatingStatus(true);
      const updated = await ordersService.updateStatus(orderId, newStatus);
      success(`Order status updated to ${newStatus}`, 'Status Updated');
      setSelectedOrder(updated);
      await loadDashboardData();
    } catch (err) {
      error(err.message, 'Status Update Failed');
    } finally {
      setIsUpdatingStatus(false);
    }
  };

  if (isLoading || !data) {
    return <LoadingSpinner message="Assembling executive metrics..." fullHeight />;
  }

  const { totals, revenueTrend, statusBreakdown, topProducts, recentOrders } = data;

  return (
    <div className="page-container dashboard-page">
      {/* Top Banner / Welcome */}
      <div className="dashboard-welcome-banner">
        <div className="welcome-text">
          <h2 className="welcome-title">Enterprise Operations Center</h2>
          <p className="welcome-desc">
            Real-time multi-channel overview of orders, inventory valuation, and tier discounts.
          </p>
        </div>
        <div className="welcome-actions">
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => navigate('/orders')}
          >
            <ShoppingCart size={16} />
            <span>Manage Orders</span>
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navigate('/products')}
          >
            <Package size={16} />
            <span>Catalog</span>
          </button>
        </div>
      </div>

      {/* KPI Stat Cards Grid */}
      <div className="stats-grid">
        <StatCard
          title="Total Net Revenue"
          value={formatCurrency(totals.revenue)}
          trend={totals.revenueDelta}
          trendLabel="vs previous period"
          subtitle="Excludes cancelled orders"
          icon={DollarSign}
          colorScheme="blue"
          onClick={() => navigate('/analytics')}
        />

        <StatCard
          title="Total Orders"
          value={formatNumber(totals.orders)}
          subtitle={`${totals.pending} pending fulfillment`}
          icon={ShoppingCart}
          colorScheme="navy"
          onClick={() => navigate('/orders')}
        />

        <StatCard
          title="Active Customers"
          value={formatNumber(totals.customers)}
          subtitle={`${totals.platinum} Platinum tier members`}
          icon={Users}
          colorScheme="purple"
          onClick={() => navigate('/customers')}
        />

        <StatCard
          title="Catalog Products"
          value={formatNumber(totals.products)}
          subtitle={
            totals.lowStock > 0 ? `${totals.lowStock} items low in stock` : 'Healthy stock levels'
          }
          icon={Boxes}
          colorScheme={totals.lowStock > 0 ? 'amber' : 'emerald'}
          onClick={() => navigate('/products')}
        />
      </div>

      {/* Analytics Charts Grid */}
      <div className="charts-grid-two-column">
        {/* Revenue Analytics Chart */}
        <div className="dashboard-card chart-card">
          <div className="dashboard-card-header">
            <div>
              <h3 className="card-title">Revenue Trajectory (Last 6 Months)</h3>
              <p className="card-subtitle">Monthly net processed billing and trends</p>
            </div>
            <button
              type="button"
              className="card-header-link"
              onClick={() => navigate('/analytics')}
            >
              <span>Detailed Analytics</span>
              <ArrowRight size={14} />
            </button>
          </div>
          <div className="card-body">
            <RevenueTrendChart data={revenueTrend} height={280} />
          </div>
        </div>

        {/* Order Status Breakdown Chart */}
        <div className="dashboard-card chart-card">
          <div className="dashboard-card-header">
            <div>
              <h3 className="card-title">Order Status Distribution</h3>
              <p className="card-subtitle">Current pipeline fulfillment status</p>
            </div>
            <span className="card-header-pill">{totals.orders} Total</span>
          </div>
          <div className="card-body">
            <OrderStatusPieChart data={statusBreakdown} height={280} />
          </div>
        </div>
      </div>

      {/* Bottom Grid: Recent Orders & Top Selling Products */}
      <div className="dashboard-split-grid">
        {/* Recent Orders Card */}
        <div className="dashboard-card split-large">
          <div className="dashboard-card-header">
            <div>
              <h3 className="card-title">Recent Orders</h3>
              <p className="card-subtitle">Latest customer transactions and status</p>
            </div>
            <button
              type="button"
              className="card-header-link"
              onClick={() => navigate('/orders')}
            >
              <span>View All Orders</span>
              <ArrowRight size={14} />
            </button>
          </div>

          <div className="card-body p-0">
            <div className="table-responsive">
              <table className="data-table dashboard-table">
                <thead>
                  <tr>
                    <th>Order #</th>
                    <th>Customer</th>
                    <th>Date</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th style={{ textAlign: 'right' }}>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {recentOrders.map((order) => (
                    <tr key={order.orderId} className="tr-clickable" onClick={() => setSelectedOrder(order)}>
                      <td>
                        <span className="order-number-text">{order.orderNumber}</span>
                      </td>
                      <td>
                        <div className="cell-customer">
                          <span className="customer-name">{order.customerName}</span>
                          <TierBadge tier={order.customerTier} size="sm" />
                        </div>
                      </td>
                      <td>
                        <span className="cell-date">{formatDate(order.createdAt)}</span>
                      </td>
                      <td>
                        <span className="cell-amount">{formatCurrency(order.totalAmount)}</span>
                      </td>
                      <td>
                        <StatusBadge status={order.status} size="sm" />
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <button
                          type="button"
                          className="btn-icon-subtle"
                          onClick={(e) => {
                            e.stopPropagation();
                            setSelectedOrder(order);
                          }}
                          title="View Order Details"
                        >
                          <Eye size={15} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Top-Selling Products Card */}
        <div className="dashboard-card split-small">
          <div className="dashboard-card-header">
            <div>
              <h3 className="card-title">Top Products</h3>
              <p className="card-subtitle">By units sold to date</p>
            </div>
            <button
              type="button"
              className="card-header-link"
              onClick={() => navigate('/products')}
            >
              <span>Catalog</span>
              <ArrowRight size={14} />
            </button>
          </div>

          <div className="card-body p-0">
            <ul className="top-products-list">
              {topProducts.map((prod, idx) => (
                <li key={prod.id} className="top-product-item">
                  <div className="top-product-rank">#{idx + 1}</div>
                  <div className="top-product-details">
                    <h4 className="top-product-name">{prod.name}</h4>
                    <span className="top-product-sku">{prod.sku}</span>
                  </div>
                  <div className="top-product-stat">
                    <span className="top-product-units">{prod.units} sold</span>
                    <span className="top-product-rev">{formatCurrency(prod.revenue)}</span>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>

      {/* Order Details Modal */}
      {selectedOrder && (
        <Modal
          isOpen={Boolean(selectedOrder)}
          onClose={() => setSelectedOrder(null)}
          title={`Order ${selectedOrder.orderNumber}`}
          subtitle={`Placed on ${formatDateTime(selectedOrder.createdAt)}`}
          size="lg"
          footer={
            <div className="modal-actions-between">
              <div className="status-transition-actions">
                <span className="transition-label">Update Status:</span>
                {(STATUS_TRANSITIONS[selectedOrder.status] || []).map((target) => (
                  <button
                    key={target}
                    type="button"
                    className={`btn btn-sm ${
                      target === 'CANCELLED' ? 'btn-danger' : 'btn-primary'
                    }`}
                    disabled={isUpdatingStatus}
                    onClick={() => handleStatusChange(selectedOrder.orderId, target)}
                  >
                    <span>{target}</span>
                  </button>
                ))}
                {(STATUS_TRANSITIONS[selectedOrder.status] || []).length === 0 && (
                  <span className="text-muted text-sm">Terminal State (Cannot be changed)</span>
                )}
              </div>

              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setSelectedOrder(null)}
              >
                Close
              </button>
            </div>
          }
        >
          <div className="order-modal-content">
            {/* Customer & Status Header */}
            <div className="order-summary-grid">
              <div className="summary-block">
                <span className="summary-label">Customer</span>
                <p className="summary-value strong">{selectedOrder.customerName}</p>
                <p className="summary-sub">{selectedOrder.customerEmail}</p>
                <div className="mt-1">
                  <TierBadge tier={selectedOrder.customerTier} showDiscount />
                </div>
              </div>

              <div className="summary-block">
                <span className="summary-label">Current Status</span>
                <div className="mt-1">
                  <StatusBadge status={selectedOrder.status} size="md" />
                </div>
                <p className="summary-sub mt-2">
                  Priority Level: {selectedOrder.priorityLevel || 1}
                </p>
              </div>

              <div className="summary-block">
                <span className="summary-label">Payment Breakdown</span>
                <div className="summary-pricing-row">
                  <span>Subtotal:</span>
                  <span>{formatCurrency(selectedOrder.subtotalAmount)}</span>
                </div>
                <div className="summary-pricing-row text-emerald">
                  <span>Tier / Bulk Discount:</span>
                  <span>-{formatCurrency(selectedOrder.discountApplied)}</span>
                </div>
                <div className="summary-pricing-row">
                  <span>Shipping Fee:</span>
                  <span>
                    {selectedOrder.shippingFee === 0
                      ? 'FREE (Over $100)'
                      : formatCurrency(selectedOrder.shippingFee)}
                  </span>
                </div>
                <div className="summary-pricing-row grand-total mt-1">
                  <span>Total Amount:</span>
                  <span className="text-primary">{formatCurrency(selectedOrder.totalAmount)}</span>
                </div>
              </div>
            </div>

            {/* Line Items Table */}
            <div className="order-items-section mt-4">
              <h4 className="section-heading">Ordered Line Items</h4>
              <div className="table-responsive">
                <table className="data-table line-items-table">
                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>SKU</th>
                      <th style={{ textAlign: 'right' }}>Unit Price</th>
                      <th style={{ textAlign: 'center' }}>Qty</th>
                      <th style={{ textAlign: 'right' }}>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedOrder.items?.map((item) => (
                      <tr key={item.id || item.productId}>
                        <td className="strong">{item.productName}</td>
                        <td>
                          <span className="product-sku-pill">{item.sku}</span>
                        </td>
                        <td style={{ textAlign: 'right' }}>{formatCurrency(item.unitPrice)}</td>
                        <td style={{ textAlign: 'center' }}>{item.quantity}</td>
                        <td style={{ textAlign: 'right' }} className="strong">
                          {formatCurrency(item.subtotal)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
