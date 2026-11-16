import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  BarChart3,
  FileSpreadsheet,
  LayoutDashboard,
  Package,
  ShoppingBag,
  ShoppingCart,
  X,
  Database,
} from 'lucide-react';
import { useSettings } from '../context/SettingsContext';

const NAV_ITEMS = [
  { label: 'Dashboard', to: '/', icon: LayoutDashboard, exact: true },
  { label: 'Sales Management', to: '/sales', icon: ShoppingBag },
  { label: 'Analytics', to: '/analytics', icon: BarChart3 },
  { label: 'Products & SKUs', to: '/products', icon: Package },
  { label: 'Order Monitoring', to: '/orders', icon: ShoppingCart },
  { label: 'Business Reports', to: '/reports', icon: FileSpreadsheet },
];

export default function Sidebar({ isOpen, onClose }) {
  const { settings } = useSettings();

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && <div className="sidebar-mobile-backdrop" onClick={onClose} />}

      <aside className={`dashboard-sidebar ${isOpen ? 'sidebar-open' : ''}`}>
        <div className="sidebar-header">
          <div className="brand-container">
            <div className="brand-logo-icon" style={{ backgroundColor: '#ff9900', color: '#131921' }}>
              <ShoppingBag size={22} />
            </div>
            <div className="brand-text">
              <h1 className="brand-title">Amazon Sales</h1>
              <span className="brand-subtitle">Analytics & Management</span>
            </div>
          </div>

          <button
            type="button"
            className="sidebar-close-btn"
            onClick={onClose}
            aria-label="Close Sidebar"
          >
            <X size={18} />
          </button>
        </div>

        <nav className="sidebar-nav">
          <div className="nav-section-label">MAIN NAVIGATION</div>
          <ul className="nav-list">
            {NAV_ITEMS.map((item) => {
              const Icon = item.icon;
              return (
                <li key={item.to} className="nav-item">
                  <NavLink
                    to={item.to}
                    end={item.exact}
                    onClick={() => {
                      if (window.innerWidth < 1024) onClose();
                    }}
                    className={({ isActive }) =>
                      `nav-link ${isActive ? 'nav-link-active' : ''}`
                    }
                  >
                    <Icon size={18} className="nav-icon" />
                    <span className="nav-label">{item.label}</span>
                  </NavLink>
                </li>
              );
            })}
          </ul>
        </nav>

        <div className="sidebar-footer">
          <div className="system-status-card">
            <div className="status-indicator-row">
              <span className="status-ping">
                <span className="status-ping-dot" style={{ backgroundColor: '#10b981' }} />
              </span>
              <span className="status-text" style={{ fontWeight: 600 }}>
                Spring Boot & PostgreSQL
              </span>
            </div>
            <p className="status-subtext" style={{ fontSize: '0.75rem', marginTop: '4px' }}>
              Table: public.amazon_sales
            </p>
          </div>
        </div>
      </aside>
    </>
  );
}
