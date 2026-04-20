# 11. Contratos de Tipagem TypeScript (Interface Front-end)

[cite_start]Este documento define as interfaces que espelham os DTOs do Back-end. [cite: 223]

## 1. Autenticação e Usuário
export type UserRole = 'ROLE_CLIENT' | 'ROLE_PROVIDER';

export interface AuthResponse {
  token: string;
  type: string;
  role: UserRole;
}

export interface User {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;
}

## 2. Geolocalização (Busca)
export interface NearbyProviderDTO {
  id: string;
  fullName: string;
  category: string;
  ratingAverage: number;
  distanceKm: number;
}