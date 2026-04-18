import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../screens/LoginScreen';
import RegisterScreen from '../screens/RegisterScreen';
import ProviderHomeScreen from '../screens/ProviderHomeScreen';
import ClientHomeScreen from '../screens/ClientHomeScreen';

export type RootStackParamList = {
  Login: undefined;
  Register: { role?: 'client' | 'provider' };
  ProviderHome: undefined;
  ClientHome: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function AppNavigator({ token }: { token: string | null }) {
  // TODO: Implementar lógica real de verificação de papel (role) do usuário
  const userRole = 'provider'; 

  return (
    <Stack.Navigator 
      screenOptions={{ 
        headerShown: false,
        animation: 'slide_from_right'
      }}
    >
      {!token ? (
        <>
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Register" component={RegisterScreen} />
        </>
      ) : (
        <>
          {userRole === 'provider' ? (
            <Stack.Screen name="ProviderHome" component={ProviderHomeScreen} />
          ) : (
            <Stack.Screen name="ClientHome" component={ClientHomeScreen} />
          )}
        </>
      )}
    </Stack.Navigator>
  );
}
