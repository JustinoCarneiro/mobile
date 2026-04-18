import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { useAuthStore } from './src/services/authStore';
import AppNavigator from './src/navigation';

/**
 * Componente App.
 * Restaurado para a forma funcional padrão para máxima compatibilidade
 * com o registerRootComponent do Expo Go.
 */
export default function App() {
  const { token } = useAuthStore();

  return (
    <>
      <StatusBar style="auto" />
      <AppNavigator token={token} />
    </>
  );
}
