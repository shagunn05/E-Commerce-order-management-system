export function parseDate(value) {
  if (!value) return null;
  if (value instanceof Date) return value;
  if (Array.isArray(value)) {
    const [year, month, day, hour = 0, minute = 0, second = 0] = value;
    return new Date(year, month - 1, day, hour, minute, second);
  }
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

export function monthKey(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
}

export function monthLabel(date) {
  return new Intl.DateTimeFormat('en-US', { month: 'short' }).format(date);
}

export function lastMonths(count, end = new Date()) {
  const cursor = new Date(end.getFullYear(), end.getMonth(), 1);
  const months = [];
  for (let index = count - 1; index >= 0; index -= 1) {
    months.push(new Date(cursor.getFullYear(), cursor.getMonth() - index, 1));
  }
  return months;
}
