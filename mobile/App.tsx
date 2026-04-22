import React, { useCallback, useState } from 'react';
import { StatusBar } from 'expo-status-bar';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { useAuthStore } from './src/services/authStore';
import AppNavigator from './src/navigation';

/**
 * Componente App — Arquitetura Padrão Estabilizada.
 *
 * Usamos `onReady` do NavigationContainer para garantir que o
 * contexto de navegação esteja disponível antes de renderizar as telas.
 * Isso evita a race condition do Android onde react-native-screens
 * tenta acessar o contexto antes dele estar pronto.
 */
import StorybookUIRoot from './.rnstorybook';

const SHOW_STORYBOOK = process.env.EXPO_PUBLIC_STORYBOOK === 'true';

function App() {
  const { token } = useAuthStore();
  const [isNavigationReady, setIsNavigationReady] = useState(false);

  const onNavigationReady = useCallback(() => {
    setIsNavigationReady(true);
  }, []);

  return (
    <SafeAreaProvider>
      <NavigationContainer onReady={onNavigationReady}>
        <StatusBar style="auto" />
        <AppNavigator token={token} />
      </NavigationContainer>
    </SafeAreaProvider>
  );
}

export default SHOW_STORYBOOK ? StorybookUIRoot : App;
