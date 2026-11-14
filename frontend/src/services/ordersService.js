import api from './apiClient';
import { endpoints } from './endpoints';
import { useMockData, withMockLatency } from './config';
import { orders as initialOrders } from '../data/orders';
import { STATUS_TRANSITIONS } from '../utils/domain';
import { buildOrder } from '../utils/pricing';
import { customers } from '../data/customers';

let mockOrders = [...initialOrders];

export const ordersService = {
  async getAll() {
    if (useMockData()) {
      return withMockLatency(mockOrders);
    }
    const response = await api.get(endpoints.orders.collection);
    return response.data;
  },

  async getById(id) {
    if (useMockData()) {
      const found = mockOrders.find((o) => o.orderId === Number(id));
      if (!found) throw new Error(`Order #${id} not found`);
      return withMockLatency(found);
    }
    const response = await api.get(endpoints.orders.byId(id));
    return response.data;
  },

  async getByNumber(orderNumber) {
    if (useMockData()) {
      const found = mockOrders.find((o) => o.orderNumber === orderNumber);
      if (!found) throw new Error(`Order ${orderNumber} not found`);
      return withMockLatency(found);
    }
    const response = await api.get(endpoints.orders.byNumber(orderNumber));
    return response.data;
  },

  async getByCustomer(customerId) {
    if (useMockData()) {
      const filtered = mockOrders.filter((o) => o.customerId === Number(customerId));
      return withMockLatency(filtered);
    }
    const response = await api.get(endpoints.orders.byCustomer(customerId));
    return response.data;
  },

  async updateStatus(id, newStatus) {
    if (useMockData()) {
      const order = mockOrders.find((o) => o.orderId === Number(id));
      if (!order) throw new Error(`Order #${id} not found`);

      const allowed = STATUS_TRANSITIONS[order.status] || [];
      if (!allowed.includes(newStatus)) {
        throw new Error(`Illegal state transition from ${order.status} to ${newStatus}`);
      }

      order.status = newStatus;
      return withMockLatency(order);
    }

    let url;
    if (newStatus === 'CANCELLED') url = endpoints.orders.cancel(id);
    else if (newStatus === 'SHIPPED') url = endpoints.orders.ship(id);
    else if (newStatus === 'DELIVERED') url = endpoints.orders.deliver(id);
    else {
      // Fallback for general status update
      url = `${endpoints.orders.byId(id)}/status?status=${newStatus}`;
    }

    const response = await api.put(url);
    return response.data;
  },

  async dispatchNext() {
    if (useMockData()) {
      // Find highest priority PENDING or PROCESSING order
      const pendingOrders = mockOrders
        .filter((o) => o.status === 'PENDING' || o.status === 'PROCESSING')
        .sort((a, b) => (b.priorityLevel || 1) - (a.priorityLevel || 1));

      if (pendingOrders.length === 0) {
        throw new Error('Fulfillment queue is currently empty.');
      }

      const target = pendingOrders[0];
      if (target.status === 'PENDING') {
        target.status = 'PROCESSING';
      } else if (target.status === 'PROCESSING') {
        target.status = 'SHIPPED';
      }
      return withMockLatency(target);
    }
    const response = await api.post(endpoints.orders.dispatchNext);
    return response.data;
  },

  async getFulfillmentQueue() {
    if (useMockData()) {
      const queue = mockOrders
        .filter((o) => o.status === 'PENDING' || o.status === 'PROCESSING')
        .sort((a, b) => (b.priorityLevel || 1) - (a.priorityLevel || 1))
        .map((o) => ({
          orderId: o.orderId,
          orderNumber: o.orderNumber,
          customerName: o.customerName,
          customerTier: o.customerTier,
          priorityLevel: o.priorityLevel,
          totalAmount: o.totalAmount,
          itemCount: o.items?.length || 0,
          status: o.status,
          enqueuedAt: o.createdAt,
        }));
      return withMockLatency(queue);
    }
    const response = await api.get(endpoints.orders.fulfillmentQueue);
    return response.data;
  },

  async create(orderData) {
    if (useMockData()) {
      const cust = customers.find((c) => c.id === Number(orderData.customerId)) || {
        id: orderData.customerId,
        name: 'Walk-in Customer',
        email: 'customer@apexcommerce.com',
        tier: 'REGULAR',
      };

      const newId = Math.max(0, ...mockOrders.map((o) => o.orderId)) + 1;
      const orderNum = `ORD-2026-${1000 + newId}`;
      const newOrder = buildOrder({
        id: newId,
        orderNumber: orderNum,
        customer: cust,
        status: 'PENDING',
        createdAt: new Date().toISOString(),
        lines: orderData.items || [],
      });

      mockOrders = [newOrder, ...mockOrders];
      return withMockLatency(newOrder);
    }
    const response = await api.post(endpoints.orders.collection, orderData);
    return response.data;
  },
};

export default ordersService;
