import React from 'react';
import { ChevronDown, Filter } from 'lucide-react';

export default function FilterDropdown({
  label = 'Filter',
  value,
  onChange,
  options = [],
  allLabel = 'All',
  icon: Icon = Filter,
  className = '',
}) {
  return (
    <div className={`filter-dropdown-wrapper ${className}`}>
      <div className="filter-select-container">
        <Icon size={14} className="filter-icon" />
        <select
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="filter-select"
          aria-label={label}
        >
          {allLabel && <option value="ALL">{allLabel}</option>}
          {options.map((opt) => {
            const val = typeof opt === 'string' ? opt : opt.value;
            const text = typeof opt === 'string' ? opt : opt.label;
            return (
              <option key={val} value={val}>
                {text}
              </option>
            );
          })}
        </select>
        <ChevronDown size={14} className="filter-chevron" />
      </div>
    </div>
  );
}
