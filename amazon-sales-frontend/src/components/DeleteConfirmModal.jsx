import React from 'react';
import { AlertTriangle, Trash2, X } from 'lucide-react';

export default function DeleteConfirmModal({ isOpen, onClose, onConfirm, sale, isLoading }) {
  if (!isOpen || !sale) return null;

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-content modal-sm" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{
              width: '36px',
              height: '36px',
              borderRadius: '50%',
              backgroundColor: '#fee2e2',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#dc2626'
            }}>
              <AlertTriangle size={20} />
            </div>
            <h3 className="modal-title">Delete Sales Record</h3>
          </div>
          <button type="button" className="modal-close-btn" onClick={onClose}>
            <X size={18} />
          </button>
        </div>

        <div className="modal-body">
          <p style={{ color: 'var(--color-text-main)', fontSize: '0.95rem', lineHeight: '1.5' }}>
            Are you sure you want to permanently delete record <strong>#{sale.indexId}</strong>?
          </p>
          <div style={{
            marginTop: '12px',
            padding: '12px',
            backgroundColor: 'var(--color-bg-subtle)',
            borderRadius: 'var(--radius-md)',
            fontSize: '0.85rem',
            color: 'var(--color-text-muted)'
          }}>
            <div><strong>Order ID:</strong> {sale.orderId}</div>
            <div><strong>SKU:</strong> {sale.sku}</div>
            <div><strong>Amount:</strong> ₹{sale.amount}</div>
          </div>
          <p style={{ marginTop: '12px', fontSize: '0.8rem', color: '#dc2626' }}>
            This operation will immediately delete the row in PostgreSQL and cannot be undone.
          </p>
        </div>

        <div className="modal-footer">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onClose}
            disabled={isLoading}
          >
            Cancel
          </button>
          <button
            type="button"
            className="btn"
            style={{ backgroundColor: '#dc2626', color: '#ffffff', border: 'none' }}
            onClick={onConfirm}
            disabled={isLoading}
          >
            <Trash2 size={16} />
            <span>{isLoading ? 'Deleting...' : 'Delete Permanently'}</span>
          </button>
        </div>
      </div>
    </div>
  );
}
