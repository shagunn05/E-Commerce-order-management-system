import React from 'react';
import { AlertTriangle, Calendar, Download, Edit2, Package, Trash2, Weight } from 'lucide-react';
import StatusBadge from './StatusBadge';
import { formatCurrency } from '../utils/format';
export default function ProductCard({
  product,
  onEdit,
  onDelete,
  onUpdateStock,
  lowStockThreshold = 10,
}) {
  const isLowStock = product.stockQuantity <= lowStockThreshold;
  const isOutOfStock = product.stockQuantity === 0;
  return (
    <div className={`product-card ${isOutOfStock ? 'product-out-of-stock' : ''}`}>
      <div className="product-card-top">
        <div className="product-meta-header">
          <span className="product-sku-pill">{product.sku}</span>
          <StatusBadge status={product.productType} size="sm" />
        </div>
        <span className="product-category-tag">{product.category}</span>
      </div>
      <div className="product-card-main">
        <h3 className="product-title" title={product.name}>
          {product.name}
        </h3>
        <div className="product-type-detail">
          {product.productType === 'PHYSICAL' && (
            <span className="type-detail-item">
              <Weight size={13} className="text-muted" />
              <span>{product.weightKg ? `${product.weightKg} kg` : 'Standard Weight'}</span>
            </span>
          )}
          {product.productType === 'DIGITAL' && (
            <span className="type-detail-item">
              <Download size={13} className="text-muted" />
              <span>Digital Asset / License</span>
            </span>
          )}
          {product.productType === 'SUBSCRIPTION' && (
            <span className="type-detail-item">
              <Calendar size={13} className="text-muted" />
              <span>
                {product.billingPeriodMonths === 12
                  ? 'Annual Renewal'
                  : product.billingPeriodMonths === 1
                  ? 'Monthly Billing'
                  : `Every ${product.billingPeriodMonths} Months`}
              </span>
            </span>
          )}
        </div>
      </div>
      <div className="product-card-pricing">
        <div className="product-price-block">
          <span className="price-label">Price</span>
          <span className="product-price">{formatCurrency(product.price)}</span>
        </div>
        <div className="product-stock-block">
          <span className="stock-label">Inventory</span>
          <span
            className={`stock-indicator ${
              isOutOfStock
                ? 'stock-zero'
                : isLowStock
                ? 'stock-low'
                : 'stock-good'
            }`}
          >
            {isLowStock && <AlertTriangle size={12} className="stock-warn-icon" />}
            {isOutOfStock ? 'Out of Stock' : `${product.stockQuantity} in stock`}
          </span>
        </div>
      </div>
      <div className="product-card-pricing">
        <div className="product-price-block">
          <span className="price-label">Price</span>
          <span className="product-price">{formatCurrency(product.price)}</span>
        </div>
        <div className="product-stock-block">
          <span className="stock-label">Inventory</span>
          <span
            className={`stock-indicator ${
              isOutOfStock
                ? 'stock-zero'
                : isLowStock
                ? 'stock-low'
                : 'stock-good'
            }`}
          >
            {isLowStock && <AlertTriangle size={12} className="stock-warn-icon" />}
            {isOutOfStock ? 'Out of Stock' : `${product.stockQuantity} in stock`}
          </span>
        </div>
      </div>
      <div className="product-card-actions">
        <button
          type="button"
          className="btn-card-action btn-edit"
          onClick={() => onEdit(product)}
          title="Edit Product"
        >
          <Edit2 size={14} />
          <span>Edit</span>
        </button>
         <button
          type="button"
          className="btn-card-action btn-stock"
          onClick={() => onUpdateStock(product)}
          title="Adjust Stock"
        >
          <Package size={14} />
          <span>Stock</span>
        </button>
        <button
          type="button"
          className="btn-card-action btn-delete"
          onClick={() => onDelete(product)}
          title="Delete Product"
        >
          <Trash2 size={14} />
        </button>
      </div>
    </div>
  );
}