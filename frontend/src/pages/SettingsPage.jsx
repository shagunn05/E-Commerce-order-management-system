import React, { useState } from 'react';
import {
  Bell,
  CheckCircle,
  Database,
  Globe,
  HardDrive,
  Key,
  RotateCcw,
  Save,
  Server,
  Settings,
  ShieldCheck,
  Store,
  User,
} from 'lucide-react';
import { useSettings } from '../context/SettingsContext';
import { useToast } from '../context/ToastContext';
import { endpoints } from '../services/endpoints';
import { initials } from '../utils/format';

export default function SettingsPage() {
  const { settings, updateSettings, resetSettings } = useSettings();
  const { success, info } = useToast();

  const [formState, setFormState] = useState({ ...settings });
  const [activeTab, setActiveTab] = useState('store'); // 'store' | 'profile' | 'backend'

  const handleChange = (field, value) => {
    setFormState((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    updateSettings(formState);
    success('System preferences and configurations saved successfully.', 'Settings Saved');
  };

  const handleReset = () => {
    resetSettings();
    info('System settings reset to default values.', 'Settings Reset');
  };

  return (
    <div className="page-container settings-page">
      {/* Page Header */}
      <div className="page-header">
        <div>
          <h2 className="page-title">Settings & Configuration</h2>
          <p className="page-subtitle">
            Admin credentials, store policies, loyalty thresholds, and backend integration status.
          </p>
        </div>
      </div>

      {/* Tabs */}
      <div className="settings-tabs">
        <button
          type="button"
          className={`settings-tab-btn ${activeTab === 'store' ? 'active' : ''}`}
          onClick={() => setActiveTab('store')}
        >
          <Store size={16} />
          <span>Store Information</span>
        </button>

        <button
          type="button"
          className={`settings-tab-btn ${activeTab === 'profile' ? 'active' : ''}`}
          onClick={() => setActiveTab('profile')}
        >
          <User size={16} />
          <span>Admin Profile</span>
        </button>

        <button
          type="button"
          className={`settings-tab-btn ${activeTab === 'backend' ? 'active' : ''}`}
          onClick={() => setActiveTab('backend')}
        >
          <Server size={16} />
          <span>Backend & API Endpoints</span>
        </button>
      </div>

      <form onSubmit={handleSubmit}>
        {/* Store Information Tab */}
        {activeTab === 'store' && (
          <div className="dashboard-card settings-card">
            <div className="dashboard-card-header">
              <div>
                <h3 className="card-title">Store & Business Rules</h3>
                <p className="card-subtitle">
                  Configure store identity, free shipping policies, and stock alarms
                </p>
              </div>
            </div>

            <div className="card-body">
              <div className="form-grid-two">
                <div className="form-group">
                  <label className="form-label" htmlFor="storeName">
                    Store Name
                  </label>
                  <input
                    id="storeName"
                    type="text"
                    className="form-input"
                    value={formState.storeName}
                    onChange={(e) => handleChange('storeName', e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="storeTagline">
                    Tagline / Subheading
                  </label>
                  <input
                    id="storeTagline"
                    type="text"
                    className="form-input"
                    value={formState.storeTagline}
                    onChange={(e) => handleChange('storeTagline', e.target.value)}
                  />
                </div>
              </div>

              <div className="form-grid-three mt-3">
                <div className="form-group">
                  <label className="form-label" htmlFor="currency">
                    Store Currency
                  </label>
                  <select
                    id="currency"
                    className="form-input"
                    value={formState.currency}
                    onChange={(e) => handleChange('currency', e.target.value)}
                  >
                    <option value="USD">USD ($) - US Dollar</option>
                    <option value="EUR">EUR (€) - Euro</option>
                    <option value="GBP">GBP (£) - British Pound</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="lowStockThreshold">
                    Low Stock Threshold
                  </label>
                  <input
                    id="lowStockThreshold"
                    type="number"
                    min="1"
                    className="form-input"
                    value={formState.lowStockThreshold}
                    onChange={(e) =>
                      handleChange('lowStockThreshold', parseInt(e.target.value, 10) || 10)
                    }
                  />
                  <span className="text-muted text-xs mt-1 block">
                    Trigger warning pill when units ≤ this count
                  </span>
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="freeShippingThreshold">
                    Free Shipping Threshold ($)
                  </label>
                  <input
                    id="freeShippingThreshold"
                    type="number"
                    min="0"
                    step="1"
                    className="form-input"
                    value={formState.freeShippingThreshold}
                    onChange={(e) =>
                      handleChange('freeShippingThreshold', parseFloat(e.target.value) || 100)
                    }
                  />
                  <span className="text-muted text-xs mt-1 block">
                    Orders at or above this amount receive $0 shipping
                  </span>
                </div>
              </div>

              <div className="form-checkbox-group mt-4">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={formState.enableAuditLogging}
                    onChange={(e) => handleChange('enableAuditLogging', e.target.checked)}
                  />
                  <span>Enable Request Audit Logging (Mirrors Spring Boot RequestAuditFilter)</span>
                </label>
              </div>
            </div>

            <div className="card-footer">
              <button type="button" className="btn btn-secondary" onClick={handleReset}>
                <RotateCcw size={15} />
                <span>Reset Defaults</span>
              </button>
              <button type="submit" className="btn btn-primary">
                <Save size={15} />
                <span>Save Store Settings</span>
              </button>
            </div>
          </div>
        )}

        {/* Profile Tab */}
        {activeTab === 'profile' && (
          <div className="dashboard-card settings-card">
            <div className="dashboard-card-header">
              <div>
                <h3 className="card-title">Operator Profile</h3>
                <p className="card-subtitle">Manage administrative account display and role details</p>
              </div>
            </div>

            <div className="card-body">
              <div className="profile-edit-header mb-4">
                <div className="profile-avatar-large">
                  {initials(formState.adminName || 'Admin')}
                </div>
                <div>
                  <h4 className="profile-display-name">{formState.adminName}</h4>
                  <span className="profile-display-role">{formState.adminRole}</span>
                </div>
              </div>

              <div className="form-grid-two">
                <div className="form-group">
                  <label className="form-label" htmlFor="adminName">
                    Full Name
                  </label>
                  <input
                    id="adminName"
                    type="text"
                    className="form-input"
                    value={formState.adminName}
                    onChange={(e) => handleChange('adminName', e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="adminEmail">
                    Email Address
                  </label>
                  <input
                    id="adminEmail"
                    type="email"
                    className="form-input"
                    value={formState.adminEmail}
                    onChange={(e) => handleChange('adminEmail', e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group mt-3">
                <label className="form-label" htmlFor="adminRole">
                  System Role Title
                </label>
                <input
                  id="adminRole"
                  type="text"
                  className="form-input"
                  value={formState.adminRole}
                  onChange={(e) => handleChange('adminRole', e.target.value)}
                />
              </div>
            </div>

            <div className="card-footer">
              <button type="submit" className="btn btn-primary">
                <Save size={15} />
                <span>Save Profile</span>
              </button>
            </div>
          </div>
        )}

        {/* Backend & API Tab */}
        {activeTab === 'backend' && (
          <div className="dashboard-card settings-card">
            <div className="dashboard-card-header">
              <div>
                <h3 className="card-title">Backend Architecture & REST Mapping</h3>
                <p className="card-subtitle">
                  Inspect prepared endpoints connecting to Java Spring Boot REST controllers
                </p>
              </div>
            </div>

            <div className="card-body">
              <div className="backend-info-banner">
                <div className="info-icon-wrapper">
                  <Server size={22} className="text-primary" />
                </div>
                <div>
                  <h4 className="info-title">Spring Boot Backend Synchronization</h4>
                  <p className="info-desc">
                    Your backend runs at <code>{formState.apiBaseUrl}</code> with PostgreSQL and JDBC
                    repositories. The frontend services layer in <code>services/</code> is mapped to all
                    domain endpoints.
                  </p>
                </div>
              </div>

              <div className="form-group mt-4">
                <label className="form-label" htmlFor="apiBaseUrl">
                  Configured API Base URL
                </label>
                <input
                  id="apiBaseUrl"
                  type="url"
                  className="form-input"
                  value={formState.apiBaseUrl}
                  onChange={(e) => handleChange('apiBaseUrl', e.target.value)}
                />
                <span className="text-muted text-xs mt-1 block">
                  Reads from <code>VITE_API_BASE_URL</code> in <code>.env</code> file.
                </span>
              </div>

              <div className="form-group mt-3">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={formState.useMockData}
                    onChange={(e) => handleChange('useMockData', e.target.checked)}
                  />
                  <span className="strong">Enable Reactive Mock Mode</span>
                </label>
                <span className="text-muted text-xs ml-6 block">
                  When enabled, all pages operate smoothly in-browser without requiring the Java
                  server to be running.
                </span>
              </div>

              <h4 className="section-heading mt-5">Mapped REST Endpoints</h4>
              <div className="endpoints-list-table">
                <div className="endpoint-item">
                  <span className="http-badge get">GET</span>
                  <code>/api/v1/products</code>
                  <span className="endpoint-desc">Catalog listing with category filters</span>
                </div>
                <div className="endpoint-item">
                  <span className="http-badge post">POST</span>
                  <code>/api/v1/products</code>
                  <span className="endpoint-desc">Creates Physical, Digital, or Subscription product</span>
                </div>
                <div className="endpoint-item">
                  <span className="http-badge get">GET</span>
                  <code>/api/v1/orders</code>
                  <span className="endpoint-desc">Retrieves all orders with customer details</span>
                </div>
                <div className="endpoint-item">
                  <span className="http-badge post">POST</span>
                  <code>/api/v1/orders/dispatch-next</code>
                  <span className="endpoint-desc">Pops and dispatches highest priority order</span>
                </div>
                <div className="endpoint-item">
                  <span className="http-badge put">PUT</span>
                  <code>/api/v1/orders/{'{id}'}/status</code>
                  <span className="endpoint-desc">State transition (PENDING → PROCESSING → SHIPPED)</span>
                </div>
                <div className="endpoint-item">
                  <span className="http-badge get">GET</span>
                  <code>/api/v1/customers</code>
                  <span className="endpoint-desc">Customer registry with loyalty tier progression</span>
                </div>
                <div className="endpoint-item">
                  <span className="http-badge get">GET</span>
                  <code>/api/v1/analytics/order-status-counts</code>
                  <span className="endpoint-desc">Real-time status breakdown for pie chart</span>
                </div>
                <div className="endpoint-item">
                  <span className="http-badge get">GET</span>
                  <code>/api/v1/analytics/valuation-stats</code>
                  <span className="endpoint-desc">Java DoubleSummaryStatistics pipeline</span>
                </div>
              </div>
            </div>

            <div className="card-footer">
              <button type="submit" className="btn btn-primary">
                <Save size={15} />
                <span>Save API Configurations</span>
              </button>
            </div>
          </div>
        )}
      </form>
    </div>
  );
}
