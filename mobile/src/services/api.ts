import axios from 'axios';
import { useAuthStore } from './authStore';

/**
 * Configuração central do Axios para integração com o Back-end.
 * 
 * ATENÇÃO: Ao testar em dispositivos físicos, certifique-se de que a baseURL
 * aponta para o endereço IP da sua máquina Linux na rede local, 
 * e não para 'localhost'.
 */
const api = axios.create({
  baseURL: process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para injeção automática de Token JWT
api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;
