import React, { useEffect, useState, useCallback } from 'react';
import {
  Plus,
  Search,
  Download,
  Filter,
  RefreshCw,
  Edit2,
  Trash2,
  ChevronLeft,
  ChevronRight,
  X,
  FileSpreadsheet,
  CheckCircle2,
  Building2,
  Calendar,
} from 'lucide-react';
import { salesApi } from '../api/salesApi';
import { reportsApi } from '../api/reportsApi';
import { formatCurrency, formatDate } from '../utils/format';
import { getStatusBadgeClass, AMAZON_CATEGORIES, AMAZON_STATUSES, FULFILMENT_TYPES, COURIER_STATUSES } from '../utils/domain';
import { useToast } from '../context/ToastContext';
import SaleModal from '../components/SaleModal';
import DeleteConfirmModal from '../components/DeleteConfirmModal';

export default function SalesManagement() {
  const { success, error, info } = useToast();

  // Data & Pagination State
  const [sales, setSales] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [sortBy, setSortBy] = useState('index_id');
  const [sortDirection, setSortDirection] = useState('DESC');

  // Search & Filter State
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedKeyword, setDebouncedKeyword] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [fulfilmentFilter, setFulfilmentFilter] = useState('');
  const [courierStatusFilter, setCourierStatusFilter] = useState('');
  const [minAmount, setMinAmount] = useState('');
  const [maxAmount, setMaxAmount] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [b2bFilter, setB2bFilter] = useState('');
  const [showFilters, setShowFilters] = useState(false);

  // Modals
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedSale, setSelectedSale] = useState(null);
  const [modalLoading, setModalLoading] = useState(false);

  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [saleToDelete, setSaleToDelete] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // Debounce search
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedKeyword(searchTerm);
      setPage(0);
    }, 400);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  const fetchSales = useCallback(async () => {
    try {
      setLoading(true);
      const params = {
        page,
        size,
        sortBy,
        sortDirection,
        keyword: debouncedKeyword || undefined,
        category: categoryFilter || undefined,
        status: statusFilter || undefined,
        fulfilment: fulfilmentFilter || undefined,
        courierStatus: courierStatusFilter || undefined,
        minAmount: minAmount ? parseFloat(minAmount) : undefined,
        maxAmount: maxAmount ? parseFloat(maxAmount) : undefined,
        startDate: startDate || undefined,
        endDate: endDate || undefined,
        b2b: b2bFilter !== '' ? b2bFilter === 'true' : undefined,
      };

      const res = await salesApi.getPaginated(params);
      setSales(res.content || []);
      setTotalPages(res.totalPages || 0);
      setTotalElements(res.totalElements || 0);
    } catch (err) {
      console.error('Error fetching sales:', err);
      error(err.message || 'Failed to load sales data from PostgreSQL');
    } finally {
      setLoading(false);
    }
  }, [
    page,
    size,
    sortBy,
    sortDirection,
    debouncedKeyword,
    categoryFilter,
    statusFilter,
    fulfilmentFilter,
    courierStatusFilter,
    minAmount,
    maxAmount,
    startDate,
    endDate,
    b2bFilter,
    error,
  ]);

  useEffect(() => {
    fetchSales();
  }, [fetchSales]);

  // Handle Sort
  const handleSort = (column) => {
    if (sortBy === column) {
      setSortDirection((prev) => (prev === 'ASC' ? 'DESC' : 'ASC'));
    } else {
      setSortBy(column);
      setSortDirection('DESC');
    }
    setPage(0);
  };

  // Create / Update Record
  const handleSaveSale = async (formData) => {
    try {
      setModalLoading(true);
      if (selectedSale) {
        // Update
        const updated = await salesApi.update(selectedSale.indexId, formData);
        success(`Sale record #${updated.indexId} updated successfully!`, 'Database Updated');
      } else {
        // Create
        const created = await salesApi.create(formData);
        success(`New sale record #${created.indexId} added to PostgreSQL!`, 'Record Inserted');
      }
      setIsModalOpen(false);
      setSelectedSale(null);
      fetchSales();
    } catch (err) {
      error(err.message || 'Failed to save record', 'Save Error');
    } finally {
      setModalLoading(false);
    }
  };

  // Delete Record
  const handleDeleteConfirm = async () => {
    if (!saleToDelete) return;
    try {
      setDeleteLoading(true);
      await salesApi.delete(saleToDelete.indexId);
      success(`Record #${saleToDelete.indexId} deleted permanently from PostgreSQL.`, 'Record Deleted');
      setIsDeleteOpen(false);
      setSaleToDelete(null);
      fetchSales();
    } catch (err) {
      error(err.message || 'Failed to delete record', 'Delete Error');
    } finally {
      setDeleteLoading(false);
    }
  };

  // Export CSV
  const handleExportCsv = async () => {
    try {
      info('Exporting sales records to CSV...', 'Exporting');
      await reportsApi.downloadSalesCsv({
        keyword: debouncedKeyword || undefined,
        category: categoryFilter || undefined,
        status: statusFilter || undefined,
        fulfilment: fulfilmentFilter || undefined,
      });
      success('CSV exported successfully.', 'Export Done');
    } catch (err) {
      error(err.message || 'CSV Export failed', 'Export Error');
    }
  };

  // Clear all filters
  const handleResetFilters = () => {
    setSearchTerm('');
    setCategoryFilter('');
    setStatusFilter('');
    setFulfilmentFilter('');
    setCourierStatusFilter('');
    setMinAmount('');
    setMaxAmount('');
    setStartDate('');
    setEndDate('');
    setB2bFilter('');
    setPage(0);
  };

  const hasActiveFilters =
    categoryFilter ||
    statusFilter ||
    fulfilmentFilter ||
    courierStatusFilter ||
    minAmount ||
    maxAmount ||
    startDate ||
    endDate ||
    b2bFilter !== '';

  return (
    <div className="page-container">
      {/* Top Header */}
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <div>
          <h1 className="page-title" style={{ fontSize: '1.75rem', fontWeight: 700, color: 'var(--color-text-main)' }}>
            Amazon Sales Management
          </h1>
          <p className="page-subtitle" style={{ color: 'var(--color-text-muted)', fontSize: '0.9rem' }}>
            Browse, search, add, edit, and delete sales records directly in PostgreSQL (amazon_sales)
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={fetchSales}
            title="Refresh database records"
          >
            <RefreshCw size={15} />
            <span>Refresh</span>
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleExportCsv}
            title="Download matching records as CSV"
          >
            <FileSpreadsheet size={15} />
            <span>Export CSV</span>
          </button>
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => {
              setSelectedSale(null);
              setIsModalOpen(true);
            }}
          >
            <Plus size={16} />
            <span>Add New Sale</span>
          </button>
        </div>
      </div>

      {/* Control Bar: Search & Filter Toggle */}
      <div className="card" style={{ padding: '16px', marginBottom: '16px', backgroundColor: 'var(--color-bg-surface)', borderRadius: 'var(--radius-lg)' }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center', flexWrap: 'wrap' }}>
          {/* Search Box */}
          <div style={{ position: 'relative', flex: '1', minWidth: '240px' }}>
            <Search size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--color-text-muted)' }} />
            <input
              type="text"
              className="form-input"
              style={{ paddingLeft: '36px', height: '40px' }}
              placeholder="Search by Order ID, SKU, Style, Category, City, State..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            {searchTerm && (
              <button
                type="button"
                onClick={() => setSearchTerm('')}
                style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--color-text-muted)' }}
              >
                <X size={15} />
              </button>
            )}
          </div>

          {/* Quick Category Select */}
          <select
            className="form-input form-select"
            style={{ width: '160px', height: '40px' }}
            value={categoryFilter}
            onChange={(e) => {
              setCategoryFilter(e.target.value);
              setPage(0);
            }}
          >
            <option value="">All Categories</option>
            {AMAZON_CATEGORIES.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>

          {/* Quick Status Select */}
          <select
            className="form-input form-select"
            style={{ width: '170px', height: '40px' }}
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPage(0);
            }}
          >
            <option value="">All Statuses</option>
            {AMAZON_STATUSES.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>

          {/* Filter Drawer Toggle */}
          <button
            type="button"
            className={`btn ${showFilters || hasActiveFilters ? 'btn-primary' : 'btn-secondary'}`}
            style={{ height: '40px' }}
            onClick={() => setShowFilters((prev) => !prev)}
          >
            <Filter size={15} />
            <span>Filters {hasActiveFilters && '• Active'}</span>
          </button>

          {hasActiveFilters && (
            <button
              type="button"
              className="btn btn-secondary"
              style={{ height: '40px', color: '#dc2626' }}
              onClick={handleResetFilters}
            >
              Reset
            </button>
          )}
        </div>

        {/* Collapsible Advanced Filters Drawer */}
        {showFilters && (
          <div style={{ marginTop: '16px', paddingTop: '16px', borderTop: '1px solid var(--color-border)', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px' }}>
            <div>
              <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-muted)', display: 'block', marginBottom: '4px' }}>Fulfilment</label>
              <select
                className="form-input form-select"
                value={fulfilmentFilter}
                onChange={(e) => { setFulfilmentFilter(e.target.value); setPage(0); }}
              >
                <option value="">All Fulfilment</option>
                {FULFILMENT_TYPES.map((f) => <option key={f} value={f}>{f}</option>)}
              </select>
            </div>

            <div>
              <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-muted)', display: 'block', marginBottom: '4px' }}>Courier Status</label>
              <select
                className="form-input form-select"
                value={courierStatusFilter}
                onChange={(e) => { setCourierStatusFilter(e.target.value); setPage(0); }}
              >
                <option value="">All Courier Status</option>
                {COURIER_STATUSES.map((cs) => <option key={cs} value={cs}>{cs}</option>)}
              </select>
            </div>

            <div>
              <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-muted)', display: 'block', marginBottom: '4px' }}>B2B Transaction</label>
              <select
                className="form-input form-select"
                value={b2bFilter}
                onChange={(e) => { setB2bFilter(e.target.value); setPage(0); }}
              >
                <option value="">All Types</option>
                <option value="true">B2B Only</option>
                <option value="false">B2C Only</option>
              </select>
            </div>

            <div>
              <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-muted)', display: 'block', marginBottom: '4px' }}>Min Amount (₹)</label>
              <input
                type="number"
                placeholder="0"
                className="form-input"
                value={minAmount}
                onChange={(e) => { setMinAmount(e.target.value); setPage(0); }}
              />
            </div>

            <div>
              <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-muted)', display: 'block', marginBottom: '4px' }}>Max Amount (₹)</label>
              <input
                type="number"
                placeholder="5000"
                className="form-input"
                value={maxAmount}
                onChange={(e) => { setMaxAmount(e.target.value); setPage(0); }}
              />
            </div>

            <div>
              <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-muted)', display: 'block', marginBottom: '4px' }}>From Date</label>
              <input
                type="date"
                className="form-input"
                value={startDate}
                onChange={(e) => { setStartDate(e.target.value); setPage(0); }}
              />
            </div>

            <div>
              <label style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--color-text-muted)', display: 'block', marginBottom: '4px' }}>To Date</label>
              <input
                type="date"
                className="form-input"
                value={endDate}
                onChange={(e) => { setEndDate(e.target.value); setPage(0); }}
              />
            </div>
          </div>
        )}
      </div>

      {/* Sales Records Table Card */}
      <div className="card" style={{ padding: '0', backgroundColor: 'var(--color-bg-surface)', borderRadius: 'var(--radius-lg)', overflow: 'hidden', boxShadow: 'var(--shadow-sm)' }}>
        <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--color-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <span style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-main)' }}>
              Showing {sales.length} of {totalElements} Total Records
            </span>
            <span style={{ marginLeft: '10px', fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
              (Page {page + 1} of {Math.max(totalPages, 1)})
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>Rows per page:</span>
            <select
              className="form-input form-select"
              style={{ width: '80px', padding: '4px 8px', height: '32px' }}
              value={size}
              onChange={(e) => {
                setSize(Number(e.target.value));
                setPage(0);
              }}
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
          </div>
        </div>

        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th onClick={() => handleSort('index_id')} style={{ cursor: 'pointer' }}>
                  # ID {sortBy === 'index_id' && (sortDirection === 'ASC' ? '▲' : '▼')}
                </th>
                <th onClick={() => handleSort('order_id')} style={{ cursor: 'pointer' }}>
                  Order ID {sortBy === 'order_id' && (sortDirection === 'ASC' ? '▲' : '▼')}
                </th>
                <th onClick={() => handleSort('date')} style={{ cursor: 'pointer' }}>
                  Date {sortBy === 'date' && (sortDirection === 'ASC' ? '▲' : '▼')}
                </th>
                <th>SKU & Style</th>
                <th onClick={() => handleSort('category')} style={{ cursor: 'pointer' }}>
                  Category {sortBy === 'category' && (sortDirection === 'ASC' ? '▲' : '▼')}
                </th>
                <th>Size</th>
                <th onClick={() => handleSort('qty')} style={{ cursor: 'pointer' }}>
                  Qty {sortBy === 'qty' && (sortDirection === 'ASC' ? '▲' : '▼')}
                </th>
                <th onClick={() => handleSort('amount')} style={{ cursor: 'pointer' }}>
                  Amount {sortBy === 'amount' && (sortDirection === 'ASC' ? '▲' : '▼')}
                </th>
                <th onClick={() => handleSort('status')} style={{ cursor: 'pointer' }}>
                  Status {sortBy === 'status' && (sortDirection === 'ASC' ? '▲' : '▼')}
                </th>
                <th>Fulfilment</th>
                <th>Courier</th>
                <th>Location</th>
                <th style={{ textAlign: 'center' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={13} style={{ textAlign: 'center', padding: '36px', color: 'var(--color-text-muted)' }}>
                    Querying PostgreSQL database...
                  </td>
                </tr>
              ) : sales.length > 0 ? (
                sales.map((sale) => (
                  <tr key={sale.indexId}>
                    <td style={{ color: 'var(--color-text-muted)', fontSize: '0.8rem' }}>{sale.indexId}</td>
                    <td style={{ fontWeight: 600, color: 'var(--color-primary)' }}>
                      {sale.orderId}
                      {sale.b2b && (
                        <span style={{ marginLeft: '6px', fontSize: '0.65rem', background: '#e0e7ff', color: '#4338ca', padding: '2px 4px', borderRadius: '3px', fontWeight: 700 }}>
                          B2B
                        </span>
                      )}
                    </td>
                    <td style={{ fontSize: '0.85rem' }}>{formatDate(sale.date)}</td>
                    <td>
                      <div style={{ fontWeight: 600, fontSize: '0.85rem' }}>{sale.sku}</div>
                      {sale.style && <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>Style: {sale.style}</div>}
                    </td>
                    <td>{sale.category}</td>
                    <td>
                      <span style={{ fontSize: '0.8rem', padding: '2px 6px', background: 'var(--color-bg-subtle)', borderRadius: '4px' }}>
                        {sale.size || '—'}
                      </span>
                    </td>
                    <td>{sale.qty}</td>
                    <td style={{ fontWeight: 600 }}>{formatCurrency(sale.amount)}</td>
                    <td>
                      <span className={`status-badge ${getStatusBadgeClass(sale.status)}`}>
                        {sale.status}
                      </span>
                    </td>
                    <td>
                      <span style={{ fontSize: '0.8rem', padding: '2px 6px', borderRadius: '4px', background: sale.fulfilment === 'Amazon' ? '#dbeafe' : '#fef3c7', color: sale.fulfilment === 'Amazon' ? '#1e40af' : '#b45309' }}>
                        {sale.fulfilment}
                      </span>
                    </td>
                    <td style={{ fontSize: '0.8rem' }}>{sale.courierStatus || '—'}</td>
                    <td style={{ fontSize: '0.8rem' }}>
                      {sale.shipCity ? `${sale.shipCity}, ${sale.shipState}` : '—'}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <div style={{ display: 'flex', gap: '6px', justifyContent: 'center' }}>
                        <button
                          type="button"
                          className="btn btn-secondary"
                          style={{ padding: '6px 8px' }}
                          title="Edit sales record"
                          onClick={() => {
                            setSelectedSale(sale);
                            setIsModalOpen(true);
                          }}
                        >
                          <Edit2 size={14} />
                        </button>
                        <button
                          type="button"
                          className="btn btn-secondary"
                          style={{ padding: '6px 8px', color: '#dc2626' }}
                          title="Delete record permanently"
                          onClick={() => {
                            setSaleToDelete(sale);
                            setIsDeleteOpen(true);
                          }}
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={13} style={{ textAlign: 'center', padding: '36px', color: 'var(--color-text-muted)' }}>
                    No matching sales records found in PostgreSQL.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Server-Side Pagination Footer */}
        <div style={{ padding: '16px 20px', borderTop: '1px solid var(--color-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)' }}>
            Showing page {page + 1} of {Math.max(totalPages, 1)} ({totalElements} items total)
          </div>

          <div style={{ display: 'flex', gap: '6px' }}>
            <button
              type="button"
              className="btn btn-secondary"
              disabled={page === 0 || loading}
              onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
            >
              <ChevronLeft size={16} />
              <span>Previous</span>
            </button>

            <button
              type="button"
              className="btn btn-secondary"
              disabled={page >= totalPages - 1 || loading}
              onClick={() => setPage((prev) => prev + 1)}
            >
              <span>Next</span>
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      </div>

      {/* Add / Edit Sale Record Modal */}
      <SaleModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedSale(null);
        }}
        onSave={handleSaveSale}
        sale={selectedSale}
        isLoading={modalLoading}
      />

      {/* Delete Confirmation Modal */}
      <DeleteConfirmModal
        isOpen={isDeleteOpen}
        onClose={() => {
          setIsDeleteOpen(false);
          setSaleToDelete(null);
        }}
        onConfirm={handleDeleteConfirm}
        sale={saleToDelete}
        isLoading={deleteLoading}
      />
    </div>
  );
}
