import React, { useEffect, useMemo, useState } from 'react';
import {
  AlertCircle,
  Calendar,
  CheckCircle2,
  Clock,
  Eye,
  Filter,
  ListOrdered,
  Package,
  RotateCcw,
  Search,
  ShoppingCart,
  Truck,
  User,
  XCircle,
  Zap,
} from 'lucide-react';
import SearchBar from '../components/SearchBar';
import FilterDropdown from '../components/FilterDropdown';
import StatusBadge from '../components/StatusBadge';
import TierBadge from '../components/TierBadge';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import ordersService from '../services/ordersService';
import { formatCurrency, formatDate, formatDateTime } from '../utils/format';
import { STATUS_TRANSITIONS } from '../utils/domain';
import { useToast } from '../context/ToastContext';

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [fulfillmentQueue, setFulfillmentQueue] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [tierFilter, setTierFilter] = useState('ALL');

  // Modals & Drawers
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [isQueueModalOpen, setIsQueueModalOpen] = useState(false);
  const [isUpdatingStatus, setIsUpdatingStatus] = useState(false);
  const [isDispatching, setIsDispatching] = useState(false);

  const { success, error, info } = useToast();

  const loadOrders = async () => {
    try {
      setIsLoading(true);
      const [allOrders, queue] = await Promise.all([
        ordersService.getAll(),
        ordersService.getFulfillmentQueue(),
      ]);
      setOrders(allOrders);
      setFulfillmentQueue(queue);
    } catch (err) {
      error(err.message, 'Failed to fetch orders');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  // Filtered Orders
  const filteredOrders = useMemo(() => {
    return orders.filter((o) => {
      const query = searchQuery.toLowerCase();
      const matchesSearch =
        !query ||
        o.orderNumber?.toLowerCase().includes(query) ||
        o.customerName?.toLowerCase().includes(query) ||
        o.customerEmail?.toLowerCase().includes(query);

      const matchesStatus = statusFilter === 'ALL' || o.status === statusFilter;
      const matchesTier = tierFilter === 'ALL' || o.customerTier === tierFilter;

      return matchesSearch && matchesStatus && matchesTier;
    });
  }, [orders, searchQuery, statusFilter, tierFilter]);

  // Handle Legal State Transition
  const handleTransitionStatus = async (orderId, targetStatus) => {
    try {
      setIsUpdatingStatus(true);
      const updated = await ordersService.updateStatus(orderId, targetStatus);
      success(
        `Order ${updated.orderNumber} transitioned to ${targetStatus}`,
        'Order Status Changed',
      );
      setSelectedOrder(updated);
      await loadOrders();
    } catch (err) {
      error(err.message, 'State Machine Error');
    } finally {
      setIsUpdatingStatus(false);
    }
  };

  // Dispatch Next Priority Order from PriorityQueue
  const handleDispatchNextPriority = async () => {
    try {
      setIsDispatching(true);
      const dispatched = await ordersService.dispatchNext();
      success(
        `Dispatched Order ${dispatched.orderNumber} for ${dispatched.customerName} (Status: ${dispatched.status})`,
        'Priority Dispatch Success',
      );
      await loadOrders();
    } catch (err) {
      info(err.message, 'Queue Status');
    } finally {
      setIsDispatching(false);
    }
  };

  // Table Columns Definition
  const orderColumns = [
    {
      key: 'orderNumber',
      header: 'Order #',
      width: '140px',
      sortable: true,
      render: (val, row) => (
        <div>
          <span className="order-number-text">{val}</span>
          <span className="text-muted text-xs block">ID: #{row.orderId}</span>
        </div>
      ),
    },
    {
      key: 'customerName',
      header: 'Customer',
      sortable: true,
      render: (val, row) => (
        <div className="cell-customer">
          <span className="customer-name">{val}</span>
          <TierBadge tier={row.customerTier} size="sm" />
        </div>
      ),
    },
    {
      key: 'createdAt',
      header: 'Order Date',
      width: '130px',
      sortable: true,
      render: (val) => <span className="cell-date">{formatDate(val)}</span>,
    },
    {
      key: 'items',
      header: 'Items',
      width: '80px',
      align: 'center',
      render: (val) => <span className="cell-items-count">{val?.length || 0}</span>,
    },
    {
      key: 'totalAmount',
      header: 'Total Amount',
      width: '120px',
      align: 'right',
      sortable: true,
      render: (val) => <span className="cell-amount strong">{formatCurrency(val)}</span>,
    },
    {
      key: 'status',
      header: 'Status',
      width: '130px',
      sortable: true,
      render: (val) => <StatusBadge status={val} size="sm" />,
    },
    {
      key: 'priorityLevel',
      header: 'Priority',
      width: '90px',
      align: 'center',
      sortable: true,
      render: (val) => (
        <span className={`priority-badge priority-${val}`}>
          P{val}
        </span>
      ),
    },
    {
      key: 'actions',
      header: 'Action',
      width: '100px',
      align: 'right',
      render: (_, row) => (
        <button
          type="button"
          className="btn-icon-subtle"
          onClick={() => setSelectedOrder(row)}
          title="View Order Details"
        >
          <Eye size={15} />
        </button>
      ),
    },
  ];

  return (
    <div className="page-container orders-page">
      {/* Page Header */}
      <div className="page-header">
        <div>
          <h2 className="page-title">Orders Management</h2>
          <p className="page-subtitle">
            Lifecycle tracking, PriorityQueue dispatching, item breakdown, and state transitions.
          </p>
        </div>

        <div className="header-actions-group">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => setIsQueueModalOpen(true)}
          >
            <ListOrdered size={16} />
            <span>Fulfillment Queue ({fulfillmentQueue.length})</span>
          </button>

          <button
            type="button"
            className="btn btn-primary"
            onClick={handleDispatchNextPriority}
            disabled={isDispatching || fulfillmentQueue.length === 0}
          >
            <Zap size={16} className={isDispatching ? 'spinner-animation' : ''} />
            <span>{isDispatching ? 'Dispatching...' : 'Dispatch Priority'}</span>
          </button>
        </div>
      </div>

      {/* Control Bar: Search & Status Filters */}
      <div className="controls-bar">
        <div className="controls-left">
          <SearchBar
            value={searchQuery}
            onChange={setSearchQuery}
            placeholder="Search by order #, customer name, email..."
            className="search-flex"
          />

          <FilterDropdown
            label="Order Status"
            value={statusFilter}
            onChange={setStatusFilter}
            options={[
              { value: 'PENDING', label: 'Pending' },
              { value: 'PROCESSING', label: 'Processing' },
              { value: 'SHIPPED', label: 'Shipped' },
              { value: 'DELIVERED', label: 'Delivered' },
              { value: 'CANCELLED', label: 'Cancelled' },
            ]}
            allLabel="All Statuses"
          />

          <FilterDropdown
            label="Customer Tier"
            value={tierFilter}
            onChange={setTierFilter}
            options={[
              { value: 'PLATINUM', label: 'Platinum' },
              { value: 'GOLD', label: 'Gold' },
              { value: 'SILVER', label: 'Silver' },
              { value: 'REGULAR', label: 'Regular' },
            ]}
            allLabel="All Loyalty Tiers"
          />
        </div>

        <div className="controls-right">
          <span className="results-count-text">
            Showing {filteredOrders.length} of {orders.length} orders
          </span>
        </div>
      </div>

      {/* Orders Table */}
      {isLoading ? (
        <LoadingSpinner message="Fetching order records..." fullHeight />
      ) : (
        <DataTable
          columns={orderColumns}
          data={filteredOrders}
          keyField="orderId"
          pageSize={10}
          initialSortField="createdAt"
          initialSortDirection="desc"
          onRowClick={(row) => setSelectedOrder(row)}
          emptyTitle="No orders match your criteria"
          emptyDescription="Try selecting a different status filter or clear your search term."
        />
      )}

      {/* Order Details & State Transition Modal */}
      {selectedOrder && (
        <Modal
          isOpen={Boolean(selectedOrder)}
          onClose={() => setSelectedOrder(null)}
          title={`Order ${selectedOrder.orderNumber}`}
          subtitle={`Placed on ${formatDateTime(selectedOrder.createdAt)}`}
          size="lg"
          footer={
            <div className="modal-actions-between">
              {/* Allowed Transitions */}
              <div className="status-transition-actions">
                <span className="transition-label">Transition State:</span>
                {(STATUS_TRANSITIONS[selectedOrder.status] || []).map((target) => (
                  <button
                    key={target}
                    type="button"
                    className={`btn btn-sm ${
                      target === 'CANCELLED' ? 'btn-danger' : 'btn-primary'
                    }`}
                    disabled={isUpdatingStatus}
                    onClick={() => handleTransitionStatus(selectedOrder.orderId, target)}
                  >
                    <span>Move to {target}</span>
                  </button>
                ))}
                {(STATUS_TRANSITIONS[selectedOrder.status] || []).length === 0 && (
                  <span className="text-muted text-xs">
                    Order is in terminal state ({selectedOrder.status}). Further transitions not
                    permitted by domain rules.
                  </span>
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
            {/* Summary Highlights */}
            <div className="order-summary-grid">
              <div className="summary-block">
                <span className="summary-label">Customer Profile</span>
                <p className="summary-value strong">{selectedOrder.customerName}</p>
                <p className="summary-sub">{selectedOrder.customerEmail}</p>
                <div className="mt-2">
                  <TierBadge tier={selectedOrder.customerTier} showDiscount />
                </div>
              </div>

              <div className="summary-block">
                <span className="summary-label">Fulfillment Status</span>
                <div className="mt-1">
                  <StatusBadge status={selectedOrder.status} size="md" />
                </div>
                <div className="priority-pill-row mt-2">
                  <span className="text-muted text-xs">Queue Priority:</span>
                  <span className={`priority-badge priority-${selectedOrder.priorityLevel || 1}`}>
                    Level {selectedOrder.priorityLevel || 1}
                  </span>
                </div>
              </div>

              <div className="summary-block">
                <span className="summary-label">Financial Breakdown</span>
                <div className="summary-pricing-row">
                  <span>Gross Subtotal:</span>
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
                      ? 'FREE (Threshold met)'
                      : formatCurrency(selectedOrder.shippingFee)}
                  </span>
                </div>
                <div className="summary-pricing-row grand-total mt-1">
                  <span>Net Total Amount:</span>
                  <span className="text-primary">{formatCurrency(selectedOrder.totalAmount)}</span>
                </div>
              </div>
            </div>

            {/* Line Items Table */}
            <div className="order-items-section mt-4">
              <h4 className="section-heading">Line Items ({selectedOrder.items?.length || 0})</h4>
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

      {/* Fulfillment Queue Inspector Modal */}
      <Modal
        isOpen={isQueueModalOpen}
        onClose={() => setIsQueueModalOpen(false)}
        title="Domain Fulfillment Queue"
        subtitle="Ordered by Customer Priority (PLATINUM=4, GOLD=3, SILVER=2, REGULAR=1)"
        size="lg"
        footer={
          <div className="modal-actions-between">
            <button
              type="button"
              className="btn btn-primary"
              onClick={handleDispatchNextPriority}
              disabled={isDispatching || fulfillmentQueue.length === 0}
            >
              <Zap size={16} />
              <span>Dispatch Highest Priority Order</span>
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsQueueModalOpen(false)}
            >
              Close
            </button>
          </div>
        }
      >
        {fulfillmentQueue.length === 0 ? (
          <EmptyState
            title="Fulfillment Queue is empty"
            description="All orders have been shipped or cancelled."
          />
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Order #</th>
                  <th>Customer</th>
                  <th>Tier</th>
                  <th style={{ textAlign: 'center' }}>Priority</th>
                  <th>Total</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {fulfillmentQueue.map((item, idx) => (
                  <tr key={item.orderId}>
                    <td className="strong">#{idx + 1}</td>
                    <td>
                      <span className="order-number-text">{item.orderNumber}</span>
                    </td>
                    <td>{item.customerName}</td>
                    <td>
                      <TierBadge tier={item.customerTier} size="sm" />
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <span className={`priority-badge priority-${item.priorityLevel}`}>
                        P{item.priorityLevel}
                      </span>
                    </td>
                    <td>{formatCurrency(item.totalAmount)}</td>
                    <td>
                      <StatusBadge status={item.status} size="sm" />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Modal>
    </div>
  );
}
