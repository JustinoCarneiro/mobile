/**
 * Contratos de Tipagem TypeScript (Interface Front-end)
 * Espelham os DTOs definidos no Back-end.
 */

export type UserRole = 'ROLE_CLIENT' | 'ROLE_PROVIDER';

export enum ServiceRequestStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
  CANCELED = 'CANCELED',
  COMPLETED = 'COMPLETED',
  PAYMENT_CONFIRMED = 'PAYMENT_CONFIRMED'
}

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

export interface ServiceRequestDTO {
  id: string;
  provider: {
    fullName: string;
    category: string;
  };
  client: {
    fullName: string;
  };
  description: string;
  mediaUrl?: string;
  status: ServiceRequestStatus;
  createdAt: string;
}
