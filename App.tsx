import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { useAuthStore } from './src/services/authStore';
import AppNavigator from './src/navigation';

/**
 * Componente App - Arquitetura Padrão Estabilizada.
 * 
 * Centralizamos os provedores aqui para garantir que o ciclo de vida
 * do App e da Navegação estejam síncronos, agora usando o motor Nativo (native-stack).
 */
export default function App() {
  const { token } = useAuthStore();

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <StatusBar style="auto" />
        <AppNavigator token={token} />
      </NavigationContainer>
    </SafeAreaProvider>
  );
}
