import React, { Suspense } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';

// Context Providers
import { SettingsProvider } from './context/SettingsContext';
import { ToastProvider } from './context/ToastContext';

// Layouts
import DashboardLayout from './layouts/DashboardLayout';

// Amazon Sales Management & Analytics Pages
import Dashboard from './pages/Dashboard';
import SalesManagement from './pages/SalesManagement';
import Analytics from './pages/Analytics';
import Products from './pages/Products';
import Orders from './pages/Orders';
import Reports from './pages/Reports';

// Global Styles
import './App.css';

// Simple Loading Fallback UI
const PageLoader = () => (
  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', flexDirection: 'column', gap: '12px' }}>
    <div className="spinner" style={{ width: '40px', height: '40px', border: '3px solid #e2e8f0', borderTopColor: '#ff9900', borderRadius: '50%', animation: 'spin 0.8s linear infinite' }} />
    <p style={{ color: '#64748b', fontSize: '0.9rem', fontWeight: 500 }}>Loading Amazon Sales Platform...</p>
  </div>
);

function App() {
  return (
    <SettingsProvider>
      <ToastProvider>
        <BrowserRouter>
          <Suspense fallback={<PageLoader />}>
            <Routes>
              {/* Main Dashboard Routes wrapped inside DashboardLayout */}
              <Route path="/" element={<DashboardLayout />}>
                <Route index element={<Dashboard />} />
                <Route path="sales" element={<SalesManagement />} />
                <Route path="analytics" element={<Analytics />} />
                <Route path="products" element={<Products />} />
                <Route path="orders" element={<Orders />} />
                <Route path="reports" element={<Reports />} />
              </Route>

              {/* Catch-all route: Redirects any unknown URL back to Dashboard */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Suspense>
        </BrowserRouter>
      </ToastProvider>
    </SettingsProvider>
  );
}

export default App;
