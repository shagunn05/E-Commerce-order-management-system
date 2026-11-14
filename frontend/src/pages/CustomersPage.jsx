import React, { useEffect, useMemo, useState } from 'react';
import {
  Award,
  Crown,
  Eye,
  Gift,
  Mail,
  Plus,
  Search,
  Shield,
  Star,
  User,
  UserCheck,
  UserPlus,
} from 'lucide-react';
import SearchBar from '../components/SearchBar';
import FilterDropdown from '../components/FilterDropdown';
import TierBadge from '../components/TierBadge';
import StatusBadge from '../components/StatusBadge';
import DataTable from '../components/DataTable';
import Modal from '../components/Modal';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import customersService from '../services/customersService';
import ordersService from '../services/ordersService';
import { formatCurrency, formatDate, formatNumber, initials } from '../utils/format';
import { nextTierGoal, TIER_MIN_POINTS } from '../utils/domain';
import { useToast } from '../context/ToastContext';

export default function CustomersPage() {
  const [customers, setCustomers] = useState([]);
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [tierFilter, setTierFilter] = useState('ALL');

  // Modals
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [customerOrders, setCustomerOrders] = useState([]);
  const [isPointsModalOpen, setIsPointsModalOpen] = useState(false);
  const [pointsInput, setPointsInput] = useState('100');

  // New Customer Form State
  const [newName, setNewName] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { success, error, warning } = useToast();

  const loadData = async () => {
    try {
      setIsLoading(true);
      const [allCustomers, allOrders] = await Promise.all([
        customersService.getAll(),
        ordersService.getAll(),
      ]);
      setCustomers(allCustomers);
      setOrders(allOrders);
    } catch (err) {
      error(err.message, 'Failed to fetch customer directory');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Compute spend stats map per customer
  const customerStatsMap = useMemo(() => {
    const map = new Map();
    orders.forEach((o) => {
      const current = map.get(o.customerId) || { totalSpend: 0, orderCount: 0 };
      if (o.status !== 'CANCELLED') {
        current.totalSpend += o.totalAmount;
      }
      current.orderCount += 1;
      map.set(o.customerId, current);
    });
    return map;
  }, [orders]);

  // Filtered Customers
  const filteredCustomers = useMemo(() => {
    return customers.filter((c) => {
      const query = searchQuery.toLowerCase();
      const matchesSearch =
        !query ||
        c.name.toLowerCase().includes(query) ||
        c.email.toLowerCase().includes(query);

      const matchesTier = tierFilter === 'ALL' || c.tier === tierFilter;
      return matchesSearch && matchesTier;
    });
  }, [customers, searchQuery, tierFilter]);

  // Open Customer Details Modal
  const handleOpenCustomerDetails = (customer) => {
    setSelectedCustomer(customer);
    const relatedOrders = orders.filter((o) => o.customerId === customer.id);
    setCustomerOrders(relatedOrders);
  };

  // Add Customer Submit
  const handleCreateCustomer = async (e) => {
    e.preventDefault();
    if (!newName.trim() || !newEmail.trim()) {
      warning('Please provide both name and email.', 'Validation Error');
      return;
    }

    try {
      setIsSubmitting(true);
      const created = await customersService.register({
        name: newName.trim(),
        email: newEmail.trim(),
      });
      success(`Customer "${created.name}" registered successfully.`, 'Customer Added');
      setIsAddModalOpen(false);
      setNewName('');
      setNewEmail('');
      await loadData();
    } catch (err) {
      error(err.message, 'Registration Failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Add Loyalty Points
  const handleAddPointsSubmit = async (e) => {
    e.preventDefault();
    if (!selectedCustomer) return;

    const points = parseInt(pointsInput, 10);
    if (Number.isNaN(points) || points <= 0) {
      warning('Points to award must be greater than zero.', 'Invalid Points');
      return;
    }

    try {
      setIsSubmitting(true);
      const updated = await customersService.addPoints(selectedCustomer.id, points);
      success(
        `Added ${points} loyalty points. Current tier: ${updated.tier}`,
        'Loyalty Points Awarded',
      );
      setSelectedCustomer(updated);
      setIsPointsModalOpen(false);
      await loadData();
    } catch (err) {
      error(err.message, 'Failed to award points');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Table Columns
  const customerColumns = [
    {
      key: 'name',
      header: 'Customer',
      sortable: true,
      render: (val, row) => (
        <div className="customer-cell-wrapper">
          <div className="customer-avatar-badge">{initials(val)}</div>
          <div>
            <span className="customer-name-text">{val}</span>
            <span className="customer-email-sub">{row.email}</span>
          </div>
        </div>
      ),
    },
    {
      key: 'tier',
      header: 'Loyalty Tier',
      width: '150px',
      sortable: true,
      render: (val) => <TierBadge tier={val} showDiscount size="sm" />,
    },
    {
      key: 'loyaltyPoints',
      header: 'Points',
      width: '110px',
      align: 'right',
      sortable: true,
      render: (val) => (
        <span className="strong text-primary">{formatNumber(val)} pts</span>
      ),
    },
    {
      key: 'spend',
      header: 'Lifetime Spend',
      width: '130px',
      align: 'right',
      sortable: true,
      render: (_, row) => {
        const stats = customerStatsMap.get(row.id);
        return <span className="strong">{formatCurrency(stats?.totalSpend || 0)}</span>;
      },
    },
    {
      key: 'orders',
      header: 'Orders',
      width: '90px',
      align: 'center',
      render: (_, row) => {
        const stats = customerStatsMap.get(row.id);
        return <span className="cell-items-count">{stats?.orderCount || 0}</span>;
      },
    },
    {
      key: 'createdAt',
      header: 'Member Since',
      width: '130px',
      sortable: true,
      render: (val) => <span className="cell-date">{formatDate(val)}</span>,
    },
    {
      key: 'actions',
      header: 'Action',
      width: '90px',
      align: 'right',
      render: (_, row) => (
        <button
          type="button"
          className="btn-icon-subtle"
          onClick={() => handleOpenCustomerDetails(row)}
          title="View Customer Profile"
        >
          <Eye size={15} />
        </button>
      ),
    },
  ];

  return (
    <div className="page-container customers-page">
      {/* Header */}
      <div className="page-header">
        <div>
          <h2 className="page-title">Customers Directory</h2>
          <p className="page-subtitle">
            Loyalty tier progression (Regular → Silver → Gold → Platinum), spending history, and accounts.
          </p>
        </div>
        <button
          type="button"
          className="btn btn-primary"
          onClick={() => setIsAddModalOpen(true)}
        >
          <UserPlus size={16} />
          <span>Register Customer</span>
        </button>
      </div>

      {/* Controls: Search and Filters */}
      <div className="controls-bar">
        <div className="controls-left">
          <SearchBar
            value={searchQuery}
            onChange={setSearchQuery}
            placeholder="Search by customer name or email..."
            className="search-flex"
          />

          <FilterDropdown
            label="Tier Filter"
            value={tierFilter}
            onChange={setTierFilter}
            options={[
              { value: 'PLATINUM', label: 'Platinum (15% Off • 1000+ pts)' },
              { value: 'GOLD', label: 'Gold (10% Off • 500+ pts)' },
              { value: 'SILVER', label: 'Silver (5% Off • 200+ pts)' },
              { value: 'REGULAR', label: 'Regular (Standard • 0+ pts)' },
            ]}
            allLabel="All Loyalty Tiers"
          />
        </div>

        <div className="controls-right">
          <span className="results-count-text">
            {filteredCustomers.length} registered customers
          </span>
        </div>
      </div>

      {/* Table */}
      {isLoading ? (
        <LoadingSpinner message="Loading customer directory..." fullHeight />
      ) : (
        <DataTable
          columns={customerColumns}
          data={filteredCustomers}
          keyField="id"
          pageSize={10}
          initialSortField="loyaltyPoints"
          initialSortDirection="desc"
          onRowClick={handleOpenCustomerDetails}
          emptyTitle="No customers found"
          emptyDescription="Try clearing your search query or tier filter."
        />
      )}

      {/* Register Customer Modal */}
      <Modal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        title="Register New Customer"
        subtitle="Creates a new customer aggregate root with Regular loyalty tier"
        size="md"
      >
        <form onSubmit={handleCreateCustomer} className="modal-form">
          <div className="form-group">
            <label className="form-label" htmlFor="cust-name">
              Full Name <span className="text-danger">*</span>
            </label>
            <input
              id="cust-name"
              type="text"
              className="form-input"
              placeholder="e.g. Maya Chen"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="cust-email">
              Email Address <span className="text-danger">*</span>
            </label>
            <input
              id="cust-email"
              type="email"
              className="form-input"
              placeholder="e.g. maya.chen@example.com"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              required
            />
          </div>

          <div className="modal-actions-right mt-4">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsAddModalOpen(false)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Registering...' : 'Register Customer'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Customer Profile & Progression Modal */}
      {selectedCustomer && (
        <Modal
          isOpen={Boolean(selectedCustomer)}
          onClose={() => setSelectedCustomer(null)}
          title={selectedCustomer.name}
          subtitle={`Member since ${formatDate(selectedCustomer.createdAt)}`}
          size="lg"
          footer={
            <div className="modal-actions-between">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setIsPointsModalOpen(true)}
              >
                <Gift size={16} />
                <span>Simulate / Award Points</span>
              </button>

              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setSelectedCustomer(null)}
              >
                Close
              </button>
            </div>
          }
        >
          <div className="customer-modal-body">
            {/* Top Loyalty Progress Card */}
            <div className="loyalty-progress-card">
              <div className="progress-header">
                <div>
                  <span className="summary-label">Loyalty Tier Status</span>
                  <div className="mt-1">
                    <TierBadge tier={selectedCustomer.tier} showDiscount size="md" />
                  </div>
                </div>
                <div className="points-display">
                  <span className="points-number">
                    {formatNumber(selectedCustomer.loyaltyPoints)}
                  </span>
                  <span className="points-label">Total Points</span>
                </div>
              </div>

              {/* Progress Bar towards Next Tier */}
              {(() => {
                const nextGoal = nextTierGoal(selectedCustomer.loyaltyPoints);
                if (!nextGoal) {
                  return (
                    <div className="tier-max-reached mt-3">
                      <Crown size={18} className="text-primary mr-1" />
                      <span>Maximum Loyalty Tier (Platinum) achieved! 15% discount on all orders.</span>
                    </div>
                  );
                }

                const currentMin =
                  selectedCustomer.tier === 'GOLD'
                    ? TIER_MIN_POINTS.GOLD
                    : selectedCustomer.tier === 'SILVER'
                    ? TIER_MIN_POINTS.SILVER
                    : 0;

                const targetMin = TIER_MIN_POINTS[nextGoal.tier];
                const progressPct = Math.min(
                  100,
                  Math.max(
                    0,
                    ((selectedCustomer.loyaltyPoints - currentMin) / (targetMin - currentMin)) * 100,
                  ),
                );

                return (
                  <div className="tier-progress-section mt-3">
                    <div className="tier-progress-labels">
                      <span>Current: {selectedCustomer.tier}</span>
                      <span>
                        {nextGoal.remaining} pts to {nextGoal.tier}
                      </span>
                    </div>
                    <div className="tier-progress-track">
                      <div
                        className="tier-progress-fill"
                        style={{ width: `${progressPct}%` }}
                      />
                    </div>
                  </div>
                );
              })()}
            </div>

            {/* Quick Metrics */}
            <div className="customer-kpi-grid mt-4">
              <div className="kpi-mini-card">
                <span className="kpi-mini-label">Lifetime Spend</span>
                <span className="kpi-mini-val">
                  {formatCurrency(customerStatsMap.get(selectedCustomer.id)?.totalSpend || 0)}
                </span>
              </div>
              <div className="kpi-mini-card">
                <span className="kpi-mini-label">Total Orders</span>
                <span className="kpi-mini-val">
                  {customerStatsMap.get(selectedCustomer.id)?.orderCount || 0}
                </span>
              </div>
              <div className="kpi-mini-card">
                <span className="kpi-mini-label">Queue Priority</span>
                <span className="kpi-mini-val">
                  Level {selectedCustomer.tier === 'PLATINUM' ? 4 : selectedCustomer.tier === 'GOLD' ? 3 : selectedCustomer.tier === 'SILVER' ? 2 : 1}
                </span>
              </div>
            </div>

            {/* Customer Order History */}
            <div className="customer-orders-section mt-4">
              <h4 className="section-heading">Order History ({customerOrders.length})</h4>
              {customerOrders.length === 0 ? (
                <p className="text-muted text-sm">No orders placed yet by this customer.</p>
              ) : (
                <div className="table-responsive">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Order #</th>
                        <th>Date</th>
                        <th>Items</th>
                        <th>Total</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {customerOrders.map((ord) => (
                        <tr key={ord.orderId}>
                          <td>
                            <span className="order-number-text">{ord.orderNumber}</span>
                          </td>
                          <td>{formatDate(ord.createdAt)}</td>
                          <td>{ord.items?.length || 0} items</td>
                          <td className="strong">{formatCurrency(ord.totalAmount)}</td>
                          <td>
                            <StatusBadge status={ord.status} size="sm" />
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </Modal>
      )}

      {/* Award Loyalty Points Modal */}
      <Modal
        isOpen={isPointsModalOpen}
        onClose={() => setIsPointsModalOpen(false)}
        title="Simulate / Award Loyalty Points"
        subtitle={`Award points to ${selectedCustomer?.name} to test tier upgrading logic`}
        size="sm"
      >
        <form onSubmit={handleAddPointsSubmit} className="modal-form">
          <div className="form-group">
            <label className="form-label" htmlFor="points">
              Points to Add
            </label>
            <input
              id="points"
              type="number"
              min="1"
              step="50"
              className="form-input"
              value={pointsInput}
              onChange={(e) => setPointsInput(e.target.value)}
              required
              autoFocus
            />
            <span className="text-muted text-xs mt-1 block">
              Thresholds: Silver at 200 pts, Gold at 500 pts, Platinum at 1,000 pts.
            </span>
          </div>

          <div className="modal-actions-right mt-4">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsPointsModalOpen(false)}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Awarding...' : 'Add Points'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
