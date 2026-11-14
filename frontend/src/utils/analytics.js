import { lastMonths, monthKey, monthLabel, parseDate } from './dates';
import { CUSTOMER_TIERS, ORDER_STATUSES } from './domain';
import { roundMoney } from './pricing';

export function buildOverview(products, orders, customers, lowStockThreshold = 10) {
  const activeOrders = orders.filter((order) => order.status !== 'CANCELLED');
  const netRevenue = roundMoney(activeOrders.reduce((sum, order) => sum + order.totalAmount, 0));
  const months = lastMonths(6);

  const revenueTrend = months.map((monthDate) => {
    const key = monthKey(monthDate);
    const inMonth = orders.filter((order) => {
      const created = parseDate(order.createdAt);
      return created && monthKey(created) === key;
    });
    const activeMonth = inMonth.filter((order) => order.status !== 'CANCELLED');
    return {
      month: key,
      label: monthLabel(monthDate),
      revenue: roundMoney(activeMonth.reduce((sum, order) => sum + order.totalAmount, 0)),
      orders: inMonth.length,
    };
  });

  const latest = revenueTrend.at(-1)?.revenue ?? 0;
  const previous = revenueTrend.at(-2)?.revenue ?? 0;
  const revenueDelta = previous ? ((latest - previous) / previous) * 100 : null;

  const statusBreakdown = ORDER_STATUSES.map((status) => {
    const matching = orders.filter((order) => order.status === status);
    return {
      status,
      count: matching.length,
      revenue: roundMoney(matching.reduce((sum, order) => sum + order.totalAmount, 0)),
    };
  });

  const productTotals = new Map();
  activeOrders.forEach((order) => {
    order.items.forEach((item) => {
      const current = productTotals.get(item.productId) ?? {
        id: item.productId,
        name: item.productName,
        sku: item.sku,
        units: 0,
        revenue: 0,
      };
      current.units += item.quantity;
      current.revenue = roundMoney(current.revenue + item.subtotal);
      productTotals.set(item.productId, current);
    });
  });

  const categoryTotals = new Map();
  products.forEach((product) => {
    const current = categoryTotals.get(product.category) ?? 0;
    categoryTotals.set(product.category, roundMoney(current + product.price * product.stockQuantity));
  });

  const spend = new Map();
  activeOrders.forEach((order) => {
    const current = spend.get(order.customerId) ?? {
      name: order.customerName,
      tier: order.customerTier,
      total: 0,
    };
    current.total = roundMoney(current.total + order.totalAmount);
    spend.set(order.customerId, current);
  });

  const amounts = orders.map((order) => order.totalAmount);
  const recentOrders = [...orders].sort(
    (left, right) => parseDate(right.createdAt) - parseDate(left.createdAt),
  );

  return {
    totals: {
      revenue: netRevenue,
      orders: orders.length,
      customers: customers.length,
      products: products.length,
      lowStock: products.filter((product) => product.stockQuantity <= lowStockThreshold).length,
      pending: orders.filter((order) => order.status === 'PENDING').length,
      platinum: customers.filter((customer) => customer.tier === 'PLATINUM').length,
      averageOrder: activeOrders.length ? roundMoney(netRevenue / activeOrders.length) : 0,
      revenueDelta,
    },
    revenueTrend,
    statusBreakdown,
    topProducts: [...productTotals.values()].sort((left, right) => right.units - left.units).slice(0, 6),
    inventoryByCategory: [...categoryTotals.entries()]
      .map(([category, value]) => ({ category, value }))
      .sort((left, right) => right.value - left.value),
    tierBreakdown: CUSTOMER_TIERS.map((tier) => ({
      tier,
      count: customers.filter((customer) => customer.tier === tier).length,
    })),
    topCustomers: [...spend.values()].sort((left, right) => right.total - left.total).slice(0, 5),
    valuation: {
      count: amounts.length,
      sum: roundMoney(amounts.reduce((sum, amount) => sum + amount, 0)),
      min: amounts.length ? roundMoney(Math.min(...amounts)) : 0,
      max: amounts.length ? roundMoney(Math.max(...amounts)) : 0,
      average: amounts.length
        ? roundMoney(amounts.reduce((sum, amount) => sum + amount, 0) / amounts.length)
        : 0,
    },
    recentOrders: recentOrders.slice(0, 6),
  };
}
