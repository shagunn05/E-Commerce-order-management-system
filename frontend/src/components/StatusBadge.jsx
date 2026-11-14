import React from 'react';
import {
  AlertCircle,
  CheckCircle2,
  Clock,
  Package,
  RotateCcw,
  Sparkles,
  Truck,
  XCircle,
} from 'lucide-react';

const STATUS_CONFIG = {
  PENDING: {
    label: 'Pending',
    icon: Clock,
    className: 'badge-pending',
  },
  PROCESSING: {
    label: 'Processing',
    icon: RotateCcw,
    className: 'badge-processing',
  },
  SHIPPED: {
    label: 'Shipped',
    icon: Truck,
    className: 'badge-shipped',
  },
  DELIVERED: {
    label: 'Delivered',
    icon: CheckCircle2,
    className: 'badge-delivered',
  },
  CANCELLED: {
    label: 'Cancelled',
    icon: XCircle,
    className: 'badge-cancelled',
  },
  // Product Types
  PHYSICAL: {
    label: 'Physical',
    icon: Package,
    className: 'badge-physical',
  },
  DIGITAL: {
    label: 'Digital',
    icon: Sparkles,
    className: 'badge-digital',
  },
  SUBSCRIPTION: {
    label: 'Subscription',
    icon: RotateCcw,
    className: 'badge-subscription',
  },
};

export default function StatusBadge({ status, size = 'md', showIcon = true }) {
  const normalized = String(status || '').toUpperCase();
  const config = STATUS_CONFIG[normalized] || {
    label: status || 'Unknown',
    icon: AlertCircle,
    className: 'badge-default',
  };

  const IconComponent = config.icon;

  return (
    <span className={`status-badge ${config.className} badge-size-${size}`}>
      {showIcon && <IconComponent size={size === 'sm' ? 12 : 14} className="badge-icon" />}
      <span className="badge-label">{config.label}</span>
    </span>
  );
}
