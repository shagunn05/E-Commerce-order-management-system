import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  BarChart3,
  Boxes,
  Database,
  LayoutDashboard,
  Package,
  Settings,
  ShoppingCart,
  Users,
  X,
  Zap,
} from 'lucide-react';
import { useSettings } from '../context/SettingsContext';

const NAV_ITEMS = [
  { label: 'Dashboard', to: '/', icon: LayoutDashboard, exact: true },
  { label: 'Products', to: '/products', icon: Package },
  { label: 'Orders', to: '/orders', icon: ShoppingCart },
  { label: 'Customers', to: '/customers', icon: Users },
  { label: 'Analytics', to: '/analytics', icon: BarChart3 },
  { label: 'Settings', to: '/settings', icon: Settings },
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
            <div className="brand-logo-icon">
              <Boxes size={22} className="brand-icon" />
            </div>
            <div className="brand-text">
              <h1 className="brand-title">{settings.storeName || 'Apex Commerce'}</h1>
              <span className="brand-subtitle">Order Management System</span>
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
          <div className="nav-section-label">MAIN MENU</div>
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
                <span className="status-ping-dot" />
              </span>
              <span className="status-text">
                {settings.useMockData ? 'Mock Mode Enabled' : 'Connected to API'}
              </span>
            </div>
            <p className="status-subtext">
              {settings.useMockData
                ? 'Using reactive in-memory state'
                : `Target: ${settings.apiBaseUrl}`}
            </p>
          </div>
        </div>
      </aside>
    </>
  );
}
