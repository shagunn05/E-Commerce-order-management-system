import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// Response interceptor for consistent error extraction
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    let message = 'An unexpected server error occurred.';
    if (error.response?.data?.message) {
      message = error.response.data.message;
    } else if (error.message) {
      message = error.message;
    }
    console.error('[API Error]:', error.config?.url, message, error.response?.data);
    return Promise.reject(new Error(message));
  }
);

export default axiosInstance;
