export const PRODUCT_TYPES = ['PHYSICAL', 'DIGITAL', 'SUBSCRIPTION'];

export const ORDER_STATUSES = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

export const CUSTOMER_TIERS = ['REGULAR', 'SILVER', 'GOLD', 'PLATINUM'];

export const TIER_RATES = {
  REGULAR: 0,
  SILVER: 0.05,
  GOLD: 0.1,
  PLATINUM: 0.15,
};

export const TIER_PRIORITY = {
  REGULAR: 1,
  SILVER: 2,
  GOLD: 3,
  PLATINUM: 4,
};

export const TIER_MIN_POINTS = {
  REGULAR: 0,
  SILVER: 200,
  GOLD: 500,
  PLATINUM: 1000,
};

export const STATUS_TRANSITIONS = {
  PENDING: ['PROCESSING', 'CANCELLED'],
  PROCESSING: ['SHIPPED', 'CANCELLED'],
  SHIPPED: ['DELIVERED'],
  DELIVERED: [],
  CANCELLED: [],
};

export const STATUS_COLORS = {
  PENDING: '#D97706',
  PROCESSING: '#2563EB',
  SHIPPED: '#4F46E5',
  DELIVERED: '#0F9F6E',
  CANCELLED: '#E11D48',
};

export const TIER_COLORS = {
  REGULAR: '#94A3B8',
  SILVER: '#64748B',
  GOLD: '#D97706',
  PLATINUM: '#1E3A8A',
};

export function nextStatuses(status) {
  return STATUS_TRANSITIONS[status] ?? [];
}

export function nextTierGoal(points) {
  const value = Number(points) || 0;
  if (value >= TIER_MIN_POINTS.PLATINUM) return null;
  if (value >= TIER_MIN_POINTS.GOLD) {
    return { tier: 'PLATINUM', remaining: TIER_MIN_POINTS.PLATINUM - value };
  }
  if (value >= TIER_MIN_POINTS.SILVER) {
    return { tier: 'GOLD', remaining: TIER_MIN_POINTS.GOLD - value };
  }
  return { tier: 'SILVER', remaining: TIER_MIN_POINTS.SILVER - value };
}

export function prettyLabel(value) {
  const text = String(value || '').toLowerCase();
  if (!text) return '';
  return text.charAt(0).toUpperCase() + text.slice(1);
}
