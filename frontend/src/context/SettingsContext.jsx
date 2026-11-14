import React, { createContext, useContext, useEffect, useState } from 'react';

const SettingsContext = createContext(null);

const DEFAULT_SETTINGS = {
  storeName: 'Apex Commerce',
  storeTagline: 'Order & Inventory Management Suite',
  adminName: 'Shagun Sharma',
  adminEmail: 'ShagunSharma@apexcommerce.com',
  adminRole: 'Senior Operations Director',
  currency: 'USD',
  currencySymbol: '$',
  lowStockThreshold: 10,
  freeShippingThreshold: 100,
  enableEmailAlerts: true,
  enableAuditLogging: true,
  theme: 'light',
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  useMockData: import.meta.env.VITE_USE_MOCK_DATA !== 'false',
};

export function SettingsProvider({ children }) {
  const [settings, setSettings] = useState(() => {
    try {
      const stored = localStorage.getItem('apex_admin_settings');
      return stored ? { ...DEFAULT_SETTINGS, ...JSON.parse(stored) } : DEFAULT_SETTINGS;
    } catch {
      return DEFAULT_SETTINGS;
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem('apex_admin_settings', JSON.stringify(settings));
    } catch (e) {
      console.warn('Could not persist settings to localStorage:', e);
    }
  }, [settings]);

  const updateSettings = (partial) => {
    setSettings((prev) => ({ ...prev, ...partial }));
  };

  const resetSettings = () => {
    setSettings(DEFAULT_SETTINGS);
  };

  return (
    <SettingsContext.Provider value={{ settings, updateSettings, resetSettings }}>
      {children}
    </SettingsContext.Provider>
  );
}

export function useSettings() {
  const context = useContext(SettingsContext);
  if (!context) {
    throw new Error('useSettings must be used within a SettingsProvider');
  }
  return context;
}

export default SettingsContext;
