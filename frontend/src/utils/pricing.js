import { TIER_PRIORITY, TIER_RATES } from './domain';

export const FREE_SHIPPING_THRESHOLD = 100;

export function roundMoney(value) {
  return Math.round(Number(value) * 100) / 100;
}

export function shippingCostFor(product) {
  if (!product || product.productType !== 'PHYSICAL') return 0;
  const weight = Math.max(0.1, Number(product.weightKg) || 0.1);
  return roundMoney(5 + weight * 2.5);
}

export function discountFor(price, quantity, tier) {
  const rate = TIER_RATES[tier] ?? 0;
  const gross = Number(price) * Number(quantity);
  const base = gross * rate;
  const bulk = quantity >= 5 ? gross * 0.05 : 0;
  return roundMoney(base + bulk);
}

export function buildOrder({ id, orderNumber, customer, status, createdAt, lines }) {
  const items = lines.map((line, index) => ({
    id: id * 100 + index + 1,
    productId: line.product.id,
    productName: line.product.name,
    sku: line.product.sku,
    productType: line.product.productType,
    quantity: line.quantity,
    unitPrice: line.product.price,
    subtotal: roundMoney(line.product.price * line.quantity),
    shippingCost: shippingCostFor(line.product),
  }));

  const subtotalAmount = roundMoney(items.reduce((sum, item) => sum + item.subtotal, 0));
  const discountApplied = roundMoney(
    items.reduce((sum, item) => sum + discountFor(item.unitPrice, item.quantity, customer.tier), 0),
  );
  const rawShipping = roundMoney(items.reduce((sum, item) => sum + item.shippingCost, 0));
  const shippingFee = subtotalAmount >= FREE_SHIPPING_THRESHOLD ? 0 : rawShipping;
  const totalAmount = roundMoney(Math.max(0, subtotalAmount - discountApplied + shippingFee));

  return {
    orderId: id,
    orderNumber,
    customerId: customer.id,
    customerName: customer.name,
    customerEmail: customer.email,
    customerTier: customer.tier,
    status,
    subtotalAmount,
    discountApplied,
    shippingFee,
    totalAmount,
    priorityLevel: TIER_PRIORITY[customer.tier] ?? 1,
    createdAt,
    items: items.map(({ shippingCost, productType, ...item }) => item),
  };
}
