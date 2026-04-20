import { create } from 'zustand';
import { User, UserRole } from '../types';

interface AuthState {
  token: string | null;
  user: User | null;
  role: UserRole | null;
  setAuth: (token: string, role: UserRole, user?: User) => void;
  clearAuth: () => void;
  isAuthenticated: () => boolean;
}

/**
 * Store de gerenciamento de sessão utilizando Zustand.
 * Controla o token JWT e as informações básicas do perfil logado.
 */
export const useAuthStore = create<AuthState>((set, get) => ({
  token: null,
  user: null,
  role: null,

  setAuth: (token, role, user) => {
    set({ token, role, user: user || null });
  },

  clearAuth: () => {
    set({ token: null, role: null, user: null });
  },

  isAuthenticated: () => {
    return !!get().token;
  },
}));
