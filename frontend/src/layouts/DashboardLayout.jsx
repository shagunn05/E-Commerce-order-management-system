import React, { useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import Navbar from '../components/Navbar';

const PAGE_TITLES = {
  '/': 'Executive Dashboard',
  '/products': 'Products & Catalog',
  '/orders': 'Orders Management',
  '/customers': 'Customers & Loyalty',
  '/analytics': 'Business Intelligence',
  '/settings': 'System Settings',
};

export default function DashboardLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  const currentTitle = PAGE_TITLES[location.pathname] || 'E-Commerce Management';

  return (
    <div className="dashboard-root">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="dashboard-main-area">
        <Navbar
          onToggleSidebar={() => setSidebarOpen((prev) => !prev)}
          pageTitle={currentTitle}
        />

        <main className="dashboard-content">
          <Outlet />
        </main>

        <footer className="dashboard-footer">
          <div className="footer-content">
            <span>© 2026 Apex Commerce Order Management System • Enterprise Edition</span>
            <span className="footer-separator">•</span>
            <span>REST API Integration Ready</span>
          </div>
        </footer>
      </div>
    </div>
  );
}
