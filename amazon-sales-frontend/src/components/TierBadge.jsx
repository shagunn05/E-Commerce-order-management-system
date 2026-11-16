import React from 'react';
import { Award, Crown, Shield, Star } from 'lucide-react';

const TIER_CONFIG = {
  PLATINUM: {
    label: 'Platinum',
    icon: Crown,
    discount: '15% Off',
    className: 'tier-platinum',
  },
  GOLD: {
    label: 'Gold',
    icon: Award,
    discount: '10% Off',
    className: 'tier-gold',
  },
  SILVER: {
    label: 'Silver',
    icon: Star,
    discount: '5% Off',
    className: 'tier-silver',
  },
  REGULAR: {
    label: 'Regular',
    icon: Shield,
    discount: 'Standard',
    className: 'tier-regular',
  },
};

export default function TierBadge({ tier, showDiscount = false, size = 'md' }) {
  const normalized = String(tier || 'REGULAR').toUpperCase();
  const config = TIER_CONFIG[normalized] || TIER_CONFIG.REGULAR;
  const IconComponent = config.icon;

  return (
    <span className={`tier-badge ${config.className} tier-size-${size}`}>
      <IconComponent size={size === 'sm' ? 12 : 14} className="tier-icon" />
      <span className="tier-name">{config.label}</span>
      {showDiscount && <span className="tier-discount-tag">{config.discount}</span>}
    </span>
  );
}
