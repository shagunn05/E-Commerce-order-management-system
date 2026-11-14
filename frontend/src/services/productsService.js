import api from './apiClient';
import { endpoints } from './endpoints';
import { useMockData, withMockLatency } from './config';
import { products as initialProducts } from '../data/products';

// In-memory mock store for local mutations
let mockProducts = [...initialProducts];

export const productsService = {
  async getAll() {
    if (useMockData()) {
      return withMockLatency(mockProducts);
    }
    const response = await api.get(endpoints.products.collection);
    return response.data;
  },

  async getById(id) {
    if (useMockData()) {
      const found = mockProducts.find((p) => p.id === Number(id));
      if (!found) throw new Error(`Product #${id} not found`);
      return withMockLatency(found);
    }
    const response = await api.get(endpoints.products.byId(id));
    return response.data;
  },

  async getBySku(sku) {
    if (useMockData()) {
      const found = mockProducts.find((p) => p.sku?.toUpperCase() === sku?.toUpperCase());
      if (!found) throw new Error(`Product with SKU ${sku} not found`);
      return withMockLatency(found);
    }
    const response = await api.get(endpoints.products.bySku(sku));
    return response.data;
  },

  async getCategories() {
    if (useMockData()) {
      const cats = Array.from(new Set(mockProducts.map((p) => p.category))).sort();
      return withMockLatency(cats);
    }
    const response = await api.get(endpoints.products.categories);
    return response.data;
  },

  async getLowStock(threshold = 10) {
    if (useMockData()) {
      const items = mockProducts.filter((p) => p.stockQuantity <= threshold);
      return withMockLatency(items);
    }
    const response = await api.get(endpoints.products.lowStock, {
      params: { threshold },
    });
    return response.data;
  },

  async create(productData) {
    if (useMockData()) {
      const newProduct = {
        id: Math.max(0, ...mockProducts.map((p) => p.id)) + 1,
        sku: productData.sku.toUpperCase(),
        name: productData.name.trim(),
        category: productData.category.trim(),
        price: Number(productData.price),
        stockQuantity: Number(productData.stockQuantity),
        productType: productData.productType,
        weightKg: productData.productType === 'PHYSICAL' ? Number(productData.weightKg || 0.5) : null,
        downloadUrl: productData.productType === 'DIGITAL' ? productData.downloadUrl : null,
        billingPeriodMonths: productData.productType === 'SUBSCRIPTION' ? Number(productData.billingPeriodMonths || 1) : null,
        createdAt: new Date().toISOString(),
        version: 0,
      };
      mockProducts = [newProduct, ...mockProducts];
      return withMockLatency(newProduct);
    }
    const response = await api.post(endpoints.products.collection, productData);
    return response.data;
  },

  async update(id, productData) {
    if (useMockData()) {
      const index = mockProducts.findIndex((p) => p.id === Number(id));
      if (index === -1) throw new Error(`Product #${id} not found`);
      const updated = {
        ...mockProducts[index],
        ...productData,
        id: Number(id),
        price: Number(productData.price),
        stockQuantity: Number(productData.stockQuantity),
        weightKg: productData.productType === 'PHYSICAL' ? Number(productData.weightKg || mockProducts[index].weightKg) : null,
        downloadUrl: productData.productType === 'DIGITAL' ? productData.downloadUrl : null,
        billingPeriodMonths: productData.productType === 'SUBSCRIPTION' ? Number(productData.billingPeriodMonths || mockProducts[index].billingPeriodMonths || 1) : null,
        version: (mockProducts[index].version || 0) + 1,
      };
      mockProducts[index] = updated;
      return withMockLatency(updated);
    }
    // Backend supports stock update via PUT /products/{id}/stock
    if (productData.newStock !== undefined) {
      const response = await api.put(endpoints.products.stock(id), null, {
        params: { newStock: productData.newStock },
      });
      return response.data;
    }
    // Fallback if needed
    const response = await api.put(endpoints.products.byId(id), productData);
    return response.data;
  },

  async updateStock(id, newStock) {
    if (useMockData()) {
      const index = mockProducts.findIndex((p) => p.id === Number(id));
      if (index === -1) throw new Error(`Product #${id} not found`);
      mockProducts[index] = {
        ...mockProducts[index],
        stockQuantity: Number(newStock),
      };
      return withMockLatency({ productId: id, newStock, updated: true });
    }
    const response = await api.put(endpoints.products.stock(id), null, {
      params: { newStock },
    });
    return response.data;
  },

  async delete(id) {
    if (useMockData()) {
      mockProducts = mockProducts.filter((p) => p.id !== Number(id));
      return withMockLatency({ productId: id, deleted: true });
    }
    const response = await api.delete(endpoints.products.byId(id));
    return response.data;
  },
};

export default productsService;
