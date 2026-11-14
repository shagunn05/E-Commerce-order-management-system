import React from 'react';
import { Inbox } from 'lucide-react';

export default function EmptyState({
  icon: Icon = Inbox,
  title = 'No records found',
  description = 'Try adjusting your search or filters to find what you are looking for.',
  actionLabel,
  onAction,
}) {
  return (
    <div className="empty-state-card">
      <div className="empty-icon-bubble">
        <Icon size={32} className="empty-icon text-muted" />
      </div>
      <h3 className="empty-title">{title}</h3>
      {description && <p className="empty-description">{description}</p>}
      {actionLabel && onAction && (
        <button type="button" className="btn btn-primary btn-sm mt-3" onClick={onAction}>
          {actionLabel}
        </button>
      )}
    </div>
  );
}
