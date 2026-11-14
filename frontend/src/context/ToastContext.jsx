import React, { createContext, useCallback, useContext, useState } from 'react';
import { AlertCircle, CheckCircle, Info, X, XCircle } from 'lucide-react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const showToast = useCallback(
    ({ title, message, type = 'info', duration = 4000 }) => {
      const id = Date.now() + Math.random().toString(36).slice(2, 7);
      const newToast = { id, title, message, type };

      setToasts((prev) => [...prev, newToast]);

      if (duration > 0) {
        setTimeout(() => {
          removeToast(id);
        }, duration);
      }
      return id;
    },
    [removeToast],
  );

  const success = useCallback(
    (message, title = 'Success') => showToast({ type: 'success', title, message }),
    [showToast],
  );

  const error = useCallback(
    (message, title = 'Error') => showToast({ type: 'error', title, message, duration: 6000 }),
    [showToast],
  );

  const info = useCallback(
    (message, title = 'Information') => showToast({ type: 'info', title, message }),
    [showToast],
  );

  const warning = useCallback(
    (message, title = 'Warning') => showToast({ type: 'warning', title, message, duration: 5000 }),
    [showToast],
  );

  return (
    <ToastContext.Provider value={{ showToast, removeToast, success, error, info, warning }}>
      {children}
      <div className="toast-container" aria-live="polite">
        {toasts.map((toast) => {
          const icons = {
            success: <CheckCircle className="toast-icon success" size={18} />,
            error: <XCircle className="toast-icon error" size={18} />,
            warning: <AlertCircle className="toast-icon warning" size={18} />,
            info: <Info className="toast-icon info" size={18} />,
          };

          return (
            <div key={toast.id} className={`toast-card toast-${toast.type}`} role="alert">
              <div className="toast-content-wrapper">
                {icons[toast.type] || icons.info}
                <div className="toast-text">
                  {toast.title && <div className="toast-title">{toast.title}</div>}
                  <div className="toast-message">{toast.message}</div>
                </div>
              </div>
              <button
                type="button"
                className="toast-close"
                onClick={() => removeToast(toast.id)}
                aria-label="Dismiss alert"
              >
                <X size={15} />
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
}

export default ToastContext;
