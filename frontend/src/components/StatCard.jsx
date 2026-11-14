import React from 'react';
import { TrendingDown, TrendingUp } from 'lucide-react';

export default function StatCard({
  title,
  value,
  subtitle,
  icon: Icon,
  trend,
  trendLabel,
  colorScheme = 'blue', // 'blue', 'navy', 'emerald', 'amber', 'purple'
  onClick,
}) {
  const isPositive = trend > 0;
  const isNegative = trend < 0;

  return (
    <div
      className={`stat-card stat-${colorScheme} ${onClick ? 'stat-clickable' : ''}`}
      onClick={onClick}
    >
      <div className="stat-card-header">
        <span className="stat-card-title">{title}</span>
        {Icon && (
          <div className="stat-card-icon-bubble">
            <Icon size={20} className="stat-card-icon" />
          </div>
        )}
      </div>

      <div className="stat-card-body">
        <div className="stat-card-value">{value}</div>

        {(trend !== undefined || subtitle) && (
          <div className="stat-card-footer">
            {trend !== undefined && trend !== null && (
              <span
                className={`stat-trend ${
                  isPositive ? 'trend-positive' : isNegative ? 'trend-negative' : 'trend-neutral'
                }`}
              >
                {isPositive && <TrendingUp size={13} className="trend-icon" />}
                {isNegative && <TrendingDown size={13} className="trend-icon" />}
                {trend > 0 ? `+${trend.toFixed(1)}%` : `${trend?.toFixed?.(1) ?? trend}%`}
              </span>
            )}
            {subtitle && <span className="stat-card-subtitle">{subtitle}</span>}
            {trendLabel && !subtitle && <span className="stat-card-subtitle">{trendLabel}</span>}
          </div>
        )}
      </div>
    </div>
  );
}
