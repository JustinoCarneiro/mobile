/**
 * Contratos de Tipagem TypeScript (Interface Front-end)
 * Espelham os DTOs definidos no Back-end.
 */

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

export interface NearbyProviderDTO {
  id: string;
  fullName: string;
  category: string;
  ratingAverage: number;
  distanceKm: number;
}
