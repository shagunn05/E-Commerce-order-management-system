import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Bell,
  CheckCircle,
  Menu,
  RotateCcw,
  Search,
  Settings,
  ShieldCheck,
  Sparkles,
  User,
  Zap,
} from 'lucide-react';
import { useSettings } from '../context/SettingsContext';
import { useToast } from '../context/ToastContext';
import { initials } from '../utils/format';
import ordersService from '../services/ordersService';

export default function Navbar({ onToggleSidebar, pageTitle }) {
  const { settings } = useSettings();
  const { success, error, info } = useToast();
  const navigate = useNavigate();
  const [isDispatching, setIsDispatching] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const handleQuickDispatch = async () => {
    try {
      setIsDispatching(true);
      const dispatched = await ordersService.dispatchNext();
      success(
        `Order ${dispatched.orderNumber} dispatched for ${dispatched.customerName} (Status: ${dispatched.status})`,
        'Priority Queue Dispatched',
      );
    } catch (err) {
      error(err.message, 'Fulfillment Notice');
    } finally {
      setIsDispatching(false);
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
          <h2 className="navbar-title">{pageTitle || 'Enterprise Dashboard'}</h2>
          <span className="navbar-badge">v1.0 • Spring Boot Ready</span>
        </div>
      </div>

      <div className="navbar-right">
        {/* Quick Priority Dispatch Action */}
        <button
          type="button"
          className="btn-quick-dispatch"
          onClick={handleQuickDispatch}
          disabled={isDispatching}
          title="Dispatches the next highest-priority order using domain PriorityQueue logic"
        >
          <Zap size={14} className={isDispatching ? 'spinner-animation' : ''} />
          <span>{isDispatching ? 'Dispatching...' : 'Dispatch Priority'}</span>
        </button>

        {/* Settings Shortcut */}
        <button
          type="button"
          className="navbar-icon-btn"
          onClick={() => navigate('/settings')}
          aria-label="Settings"
          title="Admin Settings"
        >
          <Settings size={18} />
        </button>

        {/* User Profile Pill */}
        <div className="user-profile-wrapper">
          <button
            type="button"
            className="user-profile-btn"
            onClick={() => setShowProfileMenu((prev) => !prev)}
          >
            <div className="user-avatar-circle">
              {initials(settings.adminName || 'Admin')}
            </div>
            <div className="user-info-text">
              <span className="user-name">{settings.adminName || 'Administrator'}</span>
              <span className="user-role">{settings.adminRole || 'Operations'}</span>
            </div>
          </button>

          {showProfileMenu && (
            <div className="profile-dropdown-menu">
              <div className="profile-menu-header">
                <p className="profile-menu-name">{settings.adminName}</p>
                <p className="profile-menu-email">{settings.adminEmail}</p>
              </div>
              <div className="profile-menu-divider" />
              <button
                type="button"
                className="profile-menu-item"
                onClick={() => {
                  setShowProfileMenu(false);
                  navigate('/settings');
                }}
              >
                <User size={15} />
                <span>Account Profile</span>
              </button>
              <button
                type="button"
                className="profile-menu-item"
                onClick={() => {
                  setShowProfileMenu(false);
                  navigate('/settings');
                }}
              >
                <Settings size={15} />
                <span>Store Preferences</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
