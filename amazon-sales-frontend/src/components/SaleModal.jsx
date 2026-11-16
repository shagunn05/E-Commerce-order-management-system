import React, { useState, useEffect } from 'react';
import { X, Save, AlertCircle } from 'lucide-react';

const CATEGORIES = ['Kurta', 'Set', 'Western Dress', 'Top', 'Ethnic Dress', 'Blouse', 'Bottom', 'Dupatta'];
const STATUSES = ['Shipped', 'Shipped - Delivered to Buyer', 'Cancelled', 'Shipped - Returned to Seller', 'Pending'];
const FULFILMENTS = ['Merchant', 'Amazon'];
const COURIER_STATUSES = ['Shipped', 'Unshipped', 'Cancelled'];
const SIZES = ['XS', 'S', 'M', 'L', 'XL', 'XXL', '3XL', 'Free'];
const CHANNELS = ['Amazon.in', 'Non-Amazon'];
const SERVICE_LEVELS = ['Standard', 'Expedited'];

export default function SaleModal({ isOpen, onClose, onSave, sale, isLoading }) {
  const [formData, setFormData] = useState({
    orderId: '',
    date: new Date().toISOString().slice(0, 10),
    status: 'Shipped',
    fulfilment: 'Amazon',
    salesChannel: 'Amazon.in',
    shipServiceLevel: 'Expedited',
    style: '',
    sku: '',
    category: 'Kurta',
    size: 'M',
    asin: '',
    courierStatus: 'Shipped',
    qty: 1,
    currency: 'INR',
    amount: '499.00',
    shipCity: '',
    shipState: '',
    shipPostalCode: '',
    shipCountry: 'IN',
    promotionIds: '',
    b2b: false,
    fulfilledBy: 'Amazon',
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (sale) {
      setFormData({
        orderId: sale.orderId || '',
        date: sale.date || new Date().toISOString().slice(0, 10),
        status: sale.status || 'Shipped',
        fulfilment: sale.fulfilment || 'Amazon',
        salesChannel: sale.salesChannel || 'Amazon.in',
        shipServiceLevel: sale.shipServiceLevel || 'Expedited',
        style: sale.style || '',
        sku: sale.sku || '',
        category: sale.category || 'Kurta',
        size: sale.size || 'M',
        asin: sale.asin || '',
        courierStatus: sale.courierStatus || 'Shipped',
        qty: sale.qty ?? 1,
        currency: sale.currency || 'INR',
        amount: sale.amount ? String(sale.amount) : '0.00',
        shipCity: sale.shipCity || '',
        shipState: sale.shipState || '',
        shipPostalCode: sale.shipPostalCode || '',
        shipCountry: sale.shipCountry || 'IN',
        promotionIds: sale.promotionIds || '',
        b2b: !!sale.b2b,
        fulfilledBy: sale.fulfilledBy || '',
      });
    } else {
      setFormData({
        orderId: 'ORD-' + Math.floor(100000000 + Math.random() * 900000000),
        date: new Date().toISOString().slice(0, 10),
        status: 'Shipped',
        fulfilment: 'Amazon',
        salesChannel: 'Amazon.in',
        shipServiceLevel: 'Expedited',
        style: 'SET' + Math.floor(100 + Math.random() * 900),
        sku: '',
        category: 'Kurta',
        size: 'M',
        asin: 'B0' + Math.random().toString(36).substring(2, 10).toUpperCase(),
        courierStatus: 'Shipped',
        qty: 1,
        currency: 'INR',
        amount: '499.00',
        shipCity: '',
        shipState: '',
        shipPostalCode: '',
        shipCountry: 'IN',
        promotionIds: '',
        b2b: false,
        fulfilledBy: 'Amazon',
      });
    }
    setErrors({});
  }, [sale, isOpen]);

  if (!isOpen) return null;

  const validate = () => {
    const newErrors = {};
    if (!formData.orderId.trim()) newErrors.orderId = 'Order ID is required';
    if (!formData.date) newErrors.date = 'Date is required';
    if (!formData.sku.trim()) newErrors.sku = 'SKU is required';
    if (!formData.category.trim()) newErrors.category = 'Category is required';
    if (formData.qty < 0) newErrors.qty = 'Quantity cannot be negative';
    if (isNaN(parseFloat(formData.amount)) || parseFloat(formData.amount) < 0) {
      newErrors.amount = 'Valid positive amount is required';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    onSave({
      ...formData,
      qty: parseInt(formData.qty, 10) || 1,
      amount: parseFloat(formData.amount) || 0.0,
      b2b: !!formData.b2b,
    });
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-content modal-lg" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <h3 className="modal-title">
              {sale ? `Edit Sale Record #${sale.indexId}` : 'Add New Amazon Sale Record'}
            </h3>
            <p className="modal-subtitle">
              Directly syncs to PostgreSQL database (ecommerce_db.public.amazon_sales)
            </p>
          </div>
          <button type="button" className="modal-close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body modal-grid-2">
            {/* Order ID */}
            <div className="form-group">
              <label className="form-label">Order ID *</label>
              <input
                type="text"
                name="orderId"
                className={`form-input ${errors.orderId ? 'input-error' : ''}`}
                value={formData.orderId}
                onChange={handleChange}
                placeholder="e.g. 405-8078784-5731545"
              />
              {errors.orderId && <span className="error-text">{errors.orderId}</span>}
            </div>

            {/* Date */}
            <div className="form-group">
              <label className="form-label">Transaction Date *</label>
              <input
                type="date"
                name="date"
                className={`form-input ${errors.date ? 'input-error' : ''}`}
                value={formData.date}
                onChange={handleChange}
              />
              {errors.date && <span className="error-text">{errors.date}</span>}
            </div>

            {/* SKU */}
            <div className="form-group">
              <label className="form-label">SKU *</label>
              <input
                type="text"
                name="sku"
                className={`form-input ${errors.sku ? 'input-error' : ''}`}
                value={formData.sku}
                onChange={handleChange}
                placeholder="e.g. JNE3797-KR-XXL"
              />
              {errors.sku && <span className="error-text">{errors.sku}</span>}
            </div>

            {/* Style */}
            <div className="form-group">
              <label className="form-label">Style</label>
              <input
                type="text"
                name="style"
                className="form-input"
                value={formData.style}
                onChange={handleChange}
                placeholder="e.g. JNE3797"
              />
            </div>

            {/* Category */}
            <div className="form-group">
              <label className="form-label">Category *</label>
              <select
                name="category"
                className="form-input form-select"
                value={formData.category}
                onChange={handleChange}
              >
                {CATEGORIES.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat}
                  </option>
                ))}
              </select>
            </div>

            {/* Size */}
            <div className="form-group">
              <label className="form-label">Size</label>
              <select
                name="size"
                className="form-input form-select"
                value={formData.size}
                onChange={handleChange}
              >
                {SIZES.map((sz) => (
                  <option key={sz} value={sz}>
                    {sz}
                  </option>
                ))}
              </select>
            </div>

            {/* Quantity */}
            <div className="form-group">
              <label className="form-label">Quantity *</label>
              <input
                type="number"
                name="qty"
                min="0"
                className={`form-input ${errors.qty ? 'input-error' : ''}`}
                value={formData.qty}
                onChange={handleChange}
              />
              {errors.qty && <span className="error-text">{errors.qty}</span>}
            </div>

            {/* Amount */}
            <div className="form-group">
              <label className="form-label">Amount (₹ INR) *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                name="amount"
                className={`form-input ${errors.amount ? 'input-error' : ''}`}
                value={formData.amount}
                onChange={handleChange}
                placeholder="0.00"
              />
              {errors.amount && <span className="error-text">{errors.amount}</span>}
            </div>

            {/* Status */}
            <div className="form-group">
              <label className="form-label">Order Status</label>
              <select
                name="status"
                className="form-input form-select"
                value={formData.status}
                onChange={handleChange}
              >
                {STATUSES.map((st) => (
                  <option key={st} value={st}>
                    {st}
                  </option>
                ))}
              </select>
            </div>

            {/* Fulfilment */}
            <div className="form-group">
              <label className="form-label">Fulfilment</label>
              <select
                name="fulfilment"
                className="form-input form-select"
                value={formData.fulfilment}
                onChange={handleChange}
              >
                {FULFILMENTS.map((f) => (
                  <option key={f} value={f}>
                    {f}
                  </option>
                ))}
              </select>
            </div>

            {/* Courier Status */}
            <div className="form-group">
              <label className="form-label">Courier Status</label>
              <select
                name="courierStatus"
                className="form-input form-select"
                value={formData.courierStatus}
                onChange={handleChange}
              >
                {COURIER_STATUSES.map((cs) => (
                  <option key={cs} value={cs}>
                    {cs}
                  </option>
                ))}
              </select>
            </div>

            {/* Sales Channel */}
            <div className="form-group">
              <label className="form-label">Sales Channel</label>
              <select
                name="salesChannel"
                className="form-input form-select"
                value={formData.salesChannel}
                onChange={handleChange}
              >
                {CHANNELS.map((ch) => (
                  <option key={ch} value={ch}>
                    {ch}
                  </option>
                ))}
              </select>
            </div>

            {/* Ship Service Level */}
            <div className="form-group">
              <label className="form-label">Ship Service Level</label>
              <select
                name="shipServiceLevel"
                className="form-input form-select"
                value={formData.shipServiceLevel}
                onChange={handleChange}
              >
                {SERVICE_LEVELS.map((sl) => (
                  <option key={sl} value={sl}>
                    {sl}
                  </option>
                ))}
              </select>
            </div>

            {/* ASIN */}
            <div className="form-group">
              <label className="form-label">ASIN</label>
              <input
                type="text"
                name="asin"
                className="form-input"
                value={formData.asin}
                onChange={handleChange}
                placeholder="e.g. B0983W5HK3"
              />
            </div>

            {/* Ship City */}
            <div className="form-group">
              <label className="form-label">Shipping City</label>
              <input
                type="text"
                name="shipCity"
                className="form-input"
                value={formData.shipCity}
                onChange={handleChange}
                placeholder="e.g. BENGALURU"
              />
            </div>

            {/* Ship State */}
            <div className="form-group">
              <label className="form-label">Shipping State</label>
              <input
                type="text"
                name="shipState"
                className="form-input"
                value={formData.shipState}
                onChange={handleChange}
                placeholder="e.g. KARNATAKA"
              />
            </div>

            {/* Ship Postal Code */}
            <div className="form-group">
              <label className="form-label">Postal Code</label>
              <input
                type="text"
                name="shipPostalCode"
                className="form-input"
                value={formData.shipPostalCode}
                onChange={handleChange}
                placeholder="e.g. 560085"
              />
            </div>

            {/* Fulfilled By */}
            <div className="form-group">
              <label className="form-label">Fulfilled By</label>
              <input
                type="text"
                name="fulfilledBy"
                className="form-input"
                value={formData.fulfilledBy}
                onChange={handleChange}
                placeholder="e.g. Easy Ship / Amazon"
              />
            </div>

            {/* B2B Checkbox */}
            <div className="form-group col-span-2" style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '8px' }}>
              <input
                type="checkbox"
                id="b2b-check"
                name="b2b"
                checked={formData.b2b}
                onChange={handleChange}
                style={{ width: '18px', height: '18px', cursor: 'pointer' }}
              />
              <label htmlFor="b2b-check" className="form-label" style={{ marginBottom: 0, cursor: 'pointer' }}>
                B2B Transaction (Business to Business)
              </label>
            </div>
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
              type="submit"
              className="btn btn-primary"
              disabled={isLoading}
            >
              <Save size={16} />
              <span>{isLoading ? 'Saving...' : sale ? 'Update Record' : 'Create Record'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
