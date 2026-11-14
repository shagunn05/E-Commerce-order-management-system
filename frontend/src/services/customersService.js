import api from './apiClient';
import { endpoints } from './endpoints';
import { useMockData, withMockLatency } from './config';
import { customers as initialCustomers } from '../data/customers';

let mockCustomers = [...initialCustomers];

export const customersService = {
  async getAll() {
    if (useMockData()) {
      return withMockLatency(mockCustomers);
    }
    const response = await api.get(endpoints.customers.collection);
    return response.data;
  },

  async getById(id) {
    if (useMockData()) {
      const found = mockCustomers.find((c) => c.id === Number(id));
      if (!found) throw new Error(`Customer #${id} not found`);
      return withMockLatency(found);
    }
    const response = await api.get(endpoints.customers.byId(id));
    return response.data;
  },

  async getByEmail(email) {
    if (useMockData()) {
      const found = mockCustomers.find((c) => c.email.toLowerCase() === email.toLowerCase());
      if (!found) throw new Error(`Customer with email ${email} not found`);
      return withMockLatency(found);
    }
    const response = await api.get(endpoints.customers.byEmail(email));
    return response.data;
  },

  async register(customerData) {
    if (useMockData()) {
      const exists = mockCustomers.some(
        (c) => c.email.toLowerCase() === customerData.email.trim().toLowerCase(),
      );
      if (exists) {
        throw new Error(`Customer with email '${customerData.email}' already exists.`);
      }

      const newId = Math.max(0, ...mockCustomers.map((c) => c.id)) + 1;
      const newCustomer = {
        id: newId,
        name: customerData.name.trim(),
        email: customerData.email.trim().toLowerCase(),
        tier: 'REGULAR',
        loyaltyPoints: 0,
        createdAt: new Date().toISOString(),
        version: 0,
      };

      mockCustomers = [newCustomer, ...mockCustomers];
      return withMockLatency(newCustomer);
    }
    const response = await api.post(endpoints.customers.collection, customerData);
    return response.data;
  },

  async addPoints(id, points) {
    if (useMockData()) {
      const customer = mockCustomers.find((c) => c.id === Number(id));
      if (!customer) throw new Error(`Customer #${id} not found`);

      customer.loyaltyPoints = Math.max(0, (customer.loyaltyPoints || 0) + Number(points));
      if (customer.loyaltyPoints >= 1000) {
        customer.tier = 'PLATINUM';
      } else if (customer.loyaltyPoints >= 500) {
        customer.tier = 'GOLD';
      } else if (customer.loyaltyPoints >= 200) {
        customer.tier = 'SILVER';
      } else {
        customer.tier = 'REGULAR';
      }

      return withMockLatency(customer);
    }
    // If backend implements loyalty points addition endpoint
    const response = await api.put(`${endpoints.customers.byId(id)}/points`, null, {
      params: { points },
    });
    return response.data;
  },
};

export default customersService;
