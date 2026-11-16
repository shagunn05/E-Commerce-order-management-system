export const AMAZON_CATEGORIES = [
  'Kurta',
  'Set',
  'Western Dress',
  'Top',
  'Ethnic Dress',
  'Blouse',
  'Bottom',
  'Dupatta',
];

export const AMAZON_STATUSES = [
  'Shipped',
  'Shipped - Delivered to Buyer',
  'Delivered',
  'Cancelled',
  'Pending',
  'Shipped - Returned to Seller',
];

export const FULFILMENT_TYPES = ['Amazon', 'Merchant'];

export const COURIER_STATUSES = ['Shipped', 'Unshipped', 'Cancelled'];

export const SIZES = ['XS', 'S', 'M', 'L', 'XL', 'XXL', '3XL', 'Free'];

export const STATUS_COLORS = {
  Shipped: '#2563EB',
  'Shipped - Delivered to Buyer': '#059669',
  Delivered: '#059669',
  Cancelled: '#DC2626',
  Pending: '#D97706',
  'Shipped - Returned to Seller': '#9333EA',
  Unshipped: '#F59E0B',
};

export const CATEGORY_COLORS = {
  Set: '#1E3A8A',
  Kurta: '#0284C7',
  'Western Dress': '#7C3AED',
  Top: '#0D9488',
  'Ethnic Dress': '#D97706',
  Blouse: '#E11D48',
  Bottom: '#475569',
  Dupatta: '#059669',
};

export function getStatusBadgeClass(status) {
  if (!status) return 'badge-neutral';
  const s = status.toLowerCase();
  if (s.includes('deliver')) return 'badge-delivered';
  if (s.includes('cancel')) return 'badge-cancelled';
  if (s.includes('shipped')) return 'badge-shipped';
  if (s.includes('pending') || s.includes('unshipped')) return 'badge-pending';
  return 'badge-neutral';
}
