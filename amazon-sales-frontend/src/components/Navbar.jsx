import React from 'react';
import { Menu, Database, Download, RefreshCw } from 'lucide-react';
import { reportsApi } from '../api/reportsApi';
import { useToast } from '../context/ToastContext';

export default function Navbar({ onToggleSidebar, pageTitle, onRefresh }) {
  const { success, error } = useToast();

  const handleQuickExport = async () => {
    try {
      await reportsApi.downloadSalesCsv();
      success('Sales CSV report generated and downloaded.', 'CSV Export');
    } catch (err) {
      error(err.message || 'Failed to export CSV', 'Export Error');
    }
  };

  return (
    <header className="dashboard-navbar">
      <div className="navbar-left">
        <button
          type="button"
          className="navbar-toggle-btn"
          onClick={onToggleSidebar}
          aria-label="Toggle navigation menu"
        >
          <Menu size={20} />
        </button>
        <div className="navbar-heading">
          <h2 className="navbar-title">{pageTitle || 'Amazon Sales Analytics'}</h2>
          <span className="navbar-badge" style={{ backgroundColor: '#e0f2fe', color: '#0369a1' }}>
            PostgreSQL 16 • Spring JDBC
          </span>
        </div>
      </div>

      <div className="navbar-right">
        {onRefresh && (
          <button
            type="button"
            className="navbar-icon-btn"
            onClick={onRefresh}
            title="Refresh Data"
            aria-label="Refresh Data"
          >
            <RefreshCw size={17} />
          </button>
        )}

        <button
          type="button"
          className="btn-quick-dispatch"
          style={{ background: '#131921', color: '#ff9900' }}
          onClick={handleQuickExport}
          title="Download complete Amazon Sales CSV report"
        >
          <Download size={15} />
          <span>Export CSV</span>
        </button>

        <div className="user-profile-wrapper">
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '6px 12px', background: 'var(--color-bg-subtle)', borderRadius: '20px' }}>
            <Database size={16} color="#059669" />
            <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--color-text-main)' }}>
              ecommerce_db
            </span>
          </div>
        </div>
      </div>
    </header>
  );
}
