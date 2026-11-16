import React from 'react';
import { Loader2 } from 'lucide-react';
export default function LoadingSpinner({
  message = 'Loading data...',
  size = 28,
  fullHeight = false,
}) {
  return (
    <div className={`loading-wrapper ${fullHeight ? 'full-height' : ''}`}>
      <Loader2 className="spinner-animation text-primary" size={size} />
      {message && <p className="loading-message">{message}</p>}
    </div>
  );
}