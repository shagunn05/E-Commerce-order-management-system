export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export function useMockData() {
  return import.meta.env.VITE_USE_MOCK_DATA !== 'false';
}

const MOCK_DELAY_MS = 280;

export function withMockLatency(value) {
  return new Promise((resolve) => {
    window.setTimeout(() => resolve(structuredClone(value)), MOCK_DELAY_MS);
  });
}
