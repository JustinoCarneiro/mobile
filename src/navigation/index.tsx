import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { RootStackParamList } from './types';

// Telas
import LoginScreen from '../screens/LoginScreen';
import RegisterScreen from '../screens/RegisterScreen';
import HomeScreen from '../screens/HomeScreen';

const Stack = createStackNavigator<RootStackParamList>();

/**
 * AppNavigator Unificado.
 * Mantemos uma única árvore de navegação para evitar perda de contexto no Android.
 */
export default function AppNavigator({ token }: { token: string | null }) {
  return (
    <Stack.Navigator 
      screenOptions={{ 
        headerShown: false,
        detachPreviousScreen: false 
      }}
    >
      {token === null ? (
        <Stack.Group>
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />
        </Stack.Group>
      ) : (
        <Stack.Screen name="Home" component={HomeScreen} />
      )}
    </Stack.Navigator>
  );
}
