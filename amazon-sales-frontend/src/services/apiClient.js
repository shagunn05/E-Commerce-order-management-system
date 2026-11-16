import axios from 'axios';
// import { apiBaseUrl } from './config';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const payload = error.response?.data;
    const validation = Array.isArray(payload?.validationErrors)
      ? payload.validationErrors.filter(Boolean).join(' ')
      : '';
    const message = validation || payload?.message || error.message || 'Request failed.';
    return Promise.reject(new Error(message));
  },
);

export default api;
