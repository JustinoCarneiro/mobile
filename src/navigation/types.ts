import { NavigatorScreenParams } from '@react-navigation/native';

/**
 * Tipos para as abas inferiores (Bottom Tabs).
 */
export type HomeTabParamList = {
  Explorar: undefined;
  Pedidos: undefined;
};

/**
 * Tipagem centralizada das rotas de navegação.
 * Deve espelhar EXATAMENTE as rotas registradas no AppNavigator (index.tsx).
 */
export type RootStackParamList = {
  Login: undefined;
  Register: { role?: 'ROLE_CLIENT' | 'ROLE_PROVIDER' };
  
  // Rotas autenticadas do Cliente
  HomeTabs: NavigatorScreenParams<HomeTabParamList>;
  ServiceRequest: { providerId: string; providerName: string };
  RequestDetails: { requestId: string };
  Evaluation: { requestId: string };
  
  // Rotas autenticadas do Prestador (Placeholder)
  ProviderHome: undefined;
};
