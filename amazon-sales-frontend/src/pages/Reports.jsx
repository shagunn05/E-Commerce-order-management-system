import React, { useState, useEffect } from 'react';
import {
  FileSpreadsheet,
  Download,
  Calendar,
  Filter,
  CheckCircle2,
  TrendingUp,
  MapPin,
  Layers,
  RefreshCw,
  Printer,
  Table
} from 'lucide-react';
import { reportsApi } from '../api/reportsApi';
import { formatCurrency, formatNumber, formatDate } from '../utils/format';
import { useToast } from '../context/ToastContext';
import LoadingSpinner from '../components/LoadingSpinner';

export default function Reports() {
  const { success, error, info } = useToast();

  const [loading, setLoading] = useState(true);
  const [downloading, setDownloading] = useState(false);
  const [summaryReport, setSummaryReport] = useState(null);
  const [categoryReport, setCategoryReport] = useState([]);
  const [locationReport, setLocationReport] = useState(null);

  // Filter for CSV Export
  const [exportCategory, setExportCategory] = useState('');
  const [exportStatus, setExportStatus] = useState('');
  const [exportStartDate, setExportStartDate] = useState('');
  const [exportEndDate, setExportEndDate] = useState('');

  const loadAllReports = async () => {
    try {
      setLoading(true);
      const [sum, cat, loc] = await Promise.all([
        reportsApi.getSalesSummary(),
        reportsApi.getCategoryReport(),
        reportsApi.getLocationReport()
      ]);

      setSummaryReport(sum);
      setCategoryReport(cat || []);
      setLocationReport(loc);
    } catch (err) {
      error(err.message || 'Failed to generate business reports', 'Reports Error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAllReports();
  }, []);

  const handleExportCsv = async () => {
    try {
      setDownloading(true);
      info('Generating CSV report from PostgreSQL backend...', 'Preparing Download');
      await reportsApi.downloadSalesCsv({
        category: exportCategory || undefined,
        status: exportStatus || undefined,
        startDate: exportStartDate || undefined,
        endDate: exportEndDate || undefined
      });
      success('Amazon Sales report downloaded successfully.', 'Export Complete');
    } catch (err) {
      error(err.message || 'Failed to download report', 'Download Failed');
    } finally {
      setDownloading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="Generating executive business reports..." fullHeight />;
  }

  const kpis = summaryReport?.kpis || {};

  return (
    <div className="reports-page-container">
      {/* Header */}
      <div className="page-header-actions" style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 700, color: 'var(--color-text-main)' }}>
            Business Intelligence & Reports
          </h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--color-text-muted)', marginTop: '4px' }}>
            Generate, analyze, and export executive sales summaries, category breakdowns, and CSV data.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            type="button"
            className="btn-secondary"
            onClick={loadAllReports}
            style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
          >
            <RefreshCw size={15} />
            <span>Regenerate Reports</span>
          </button>
        </div>
      </div>

      {/* CSV Export Control Panel Card */}
      <div className="card" style={{ marginBottom: '24px', borderLeft: '4px solid #ff9900' }}>
        <div className="card-header">
          <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <FileSpreadsheet size={20} color="#ff9900" />
            Export Raw Sales Data to CSV
          </h3>
          <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>RFC-4180 Compliant Spreadsheet</span>
        </div>
        <div className="card-body">
          <p style={{ fontSize: '0.875rem', color: 'var(--color-text-muted)', marginBottom: '16px' }}>
            Download the complete or filtered dataset directly from PostgreSQL. Includes all 23 Amazon attributes: Order IDs, SKU, pricing, courier telemetry, and fulfilment data.
          </p>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px', marginBottom: '16px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, marginBottom: '4px', color: 'var(--color-text-muted)' }}>
                Filter Category
              </label>
              <select
                value={exportCategory}
                onChange={(e) => setExportCategory(e.target.value)}
                style={{ width: '100%', padding: '8px 10px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-border)', fontSize: '0.85rem' }}
              >
                <option value="">All Categories</option>
                <option value="Kurta">Kurta</option>
                <option value="Set">Set</option>
                <option value="Western Dress">Western Dress</option>
                <option value="Top">Top</option>
                <option value="Ethnic Dress">Ethnic Dress</option>
                <option value="Blouse">Blouse</option>
                <option value="Bottom">Bottom</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, marginBottom: '4px', color: 'var(--color-text-muted)' }}>
                Filter Status
              </label>
              <select
                value={exportStatus}
                onChange={(e) => setExportStatus(e.target.value)}
                style={{ width: '100%', padding: '8px 10px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-border)', fontSize: '0.85rem' }}
              >
                <option value="">All Statuses</option>
                <option value="Shipped">Shipped</option>
                <option value="Shipped - Delivered to Buyer">Delivered</option>
                <option value="Cancelled">Cancelled</option>
                <option value="Pending">Pending</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, marginBottom: '4px', color: 'var(--color-text-muted)' }}>
                From Date
              </label>
              <input
                type="date"
                value={exportStartDate}
                onChange={(e) => setExportStartDate(e.target.value)}
                style={{ width: '100%', padding: '7px 10px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-border)', fontSize: '0.85rem' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, marginBottom: '4px', color: 'var(--color-text-muted)' }}>
                To Date
              </label>
              <input
                type="date"
                value={exportEndDate}
                onChange={(e) => setExportEndDate(e.target.value)}
                style={{ width: '100%', padding: '7px 10px', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-border)', fontSize: '0.85rem' }}
              />
            </div>
          </div>

          <button
            type="button"
            className="btn-primary"
            onClick={handleExportCsv}
            disabled={downloading}
            style={{
              backgroundColor: '#ff9900',
              borderColor: '#e68a00',
              color: '#131921',
              fontWeight: 700,
              display: 'inline-flex',
              alignItems: 'center',
              gap: '8px',
              padding: '10px 20px',
              cursor: downloading ? 'not-allowed' : 'pointer'
            }}
          >
            <Download size={17} />
            <span>{downloading ? 'Exporting Dataset...' : 'Download CSV Report'}</span>
          </button>
        </div>
      </div>

      {/* Executive Summary Card */}
      <div className="card" style={{ marginBottom: '24px' }}>
        <div className="card-header">
          <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <TrendingUp size={18} color="#2563eb" />
            Executive Performance Summary
          </h3>
          <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
            Generated at: {summaryReport?.generatedAt ? new Date(summaryReport.generatedAt).toLocaleString() : 'Live'}
          </span>
        </div>
        <div className="card-body">
          <div className="stats-grid" style={{ marginBottom: '20px' }}>
            <div style={{ padding: '16px', background: 'var(--color-bg-subtle)', borderRadius: 'var(--radius-md)' }}>
              <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', display: 'block' }}>Total Gross Revenue</span>
              <strong style={{ fontSize: '1.4rem', color: '#059669', display: 'block', marginTop: '4px' }}>
                {formatCurrency(kpis.totalRevenue)}
              </strong>
            </div>

            <div style={{ padding: '16px', background: 'var(--color-bg-subtle)', borderRadius: 'var(--radius-md)' }}>
              <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', display: 'block' }}>Total Distinct Orders</span>
              <strong style={{ fontSize: '1.4rem', color: '#2563eb', display: 'block', marginTop: '4px' }}>
                {formatNumber(kpis.totalOrders)}
              </strong>
            </div>

            <div style={{ padding: '16px', background: 'var(--color-bg-subtle)', borderRadius: 'var(--radius-md)' }}>
              <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', display: 'block' }}>Total Units Fulfilled</span>
              <strong style={{ fontSize: '1.4rem', color: '#ff9900', display: 'block', marginTop: '4px' }}>
                {formatNumber(kpis.totalUnits)}
              </strong>
            </div>

            <div style={{ padding: '16px', background: 'var(--color-bg-subtle)', borderRadius: 'var(--radius-md)' }}>
              <span style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', display: 'block' }}>Average Order Value (AOV)</span>
              <strong style={{ fontSize: '1.4rem', color: '#8b5cf6', display: 'block', marginTop: '4px' }}>
                {formatCurrency(kpis.averageOrderValue)}
              </strong>
            </div>
          </div>
        </div>
      </div>

      {/* Two columns: Category Breakdown & Top Performing States */}
      <div className="grid grid-2" style={{ marginBottom: '24px' }}>
        {/* Category Performance */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Layers size={18} color="#2563eb" />
              Category Performance Report
            </h3>
          </div>
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Category</th>
                  <th style={{ textAlign: 'right' }}>Units</th>
                  <th style={{ textAlign: 'right' }}>Revenue</th>
                  <th style={{ textAlign: 'right' }}>Avg Price</th>
                </tr>
              </thead>
              <tbody>
                {categoryReport.map((cat, idx) => (
                  <tr key={cat.category || idx}>
                    <td style={{ fontWeight: 600 }}>{cat.category || 'General'}</td>
                    <td style={{ textAlign: 'right' }}>{formatNumber(cat.totalUnits)}</td>
                    <td style={{ textAlign: 'right', fontWeight: 600, color: '#059669' }}>
                      {formatCurrency(cat.totalRevenue)}
                    </td>
                    <td style={{ textAlign: 'right' }}>{formatCurrency(cat.averagePrice)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Location Intelligence */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <MapPin size={18} color="#059669" />
              Top State Performance Report
            </h3>
          </div>
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>State</th>
                  <th style={{ textAlign: 'right' }}>Orders</th>
                  <th style={{ textAlign: 'right' }}>Total Sales</th>
                </tr>
              </thead>
              <tbody>
                {(summaryReport?.topPerformingStates || locationReport?.topStates || []).map((loc, idx) => (
                  <tr key={loc.location || idx}>
                    <td style={{ fontWeight: 600 }}>{loc.location}</td>
                    <td style={{ textAlign: 'right' }}>{formatNumber(loc.orderCount)}</td>
                    <td style={{ textAlign: 'right', fontWeight: 600, color: '#059669' }}>
                      {formatCurrency(loc.totalAmount)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
