import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { View, Text } from 'react-native';

// Telas
import LoginScreen from '../screens/LoginScreen';
import RegisterScreen from '../screens/RegisterScreen';
import HomeScreen from '../screens/HomeScreen';
import MyRequestsScreen from '../screens/MyRequestsScreen';
import ServiceRequestScreen from '../screens/ServiceRequestScreen';
import RequestDetailsScreen from '../screens/RequestDetailsScreen';
import EvaluationScreen from '../screens/EvaluationScreen';
import ProviderDashboardScreen from '../screens/ProviderDashboardScreen';

// Tipos e Estado
import { RootStackParamList, HomeTabParamList } from './types';
import { useAuthStore } from '../services/authStore';

const Stack = createNativeStackNavigator<RootStackParamList>();
const Tab = createBottomTabNavigator<HomeTabParamList>();

/**
 * Tab Navigator para o Cliente.
 */
function ClientTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          height: 60,
          paddingBottom: 10,
          paddingTop: 10,
        },
        tabBarActiveTintColor: '#004595',
        tabBarInactiveTintColor: 'gray',
        tabBarLabelStyle: {
          fontWeight: 'bold',
        }
      }}
    >
      <Tab.Screen 
        name="Explorar" 
        component={HomeScreen}
        options={{
          tabBarIcon: ({ color }) => <Text style={{ color }}>🔍</Text>,
        }}
      />
      <Tab.Screen 
        name="Pedidos" 
        component={MyRequestsScreen}
        options={{
          tabBarIcon: ({ color }) => <Text style={{ color }}>📝</Text>,
        }}
      />
    </Tab.Navigator>
  );
}

export default function AppNavigator({ token }: { token: string | null }) {
  const { role } = useAuthStore();

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
          {/* Dashboard principal baseado no Role */}
          {role === 'ROLE_PROVIDER' ? (
            <Stack.Screen name="ProviderHome" component={ProviderDashboardScreen} />
          ) : (
            <Stack.Screen name="HomeTabs" component={ClientTabs} />
          )}

          {/* Telas de fluxo comum/estendidas */}
          <Stack.Screen name="ServiceRequest" component={ServiceRequestScreen} />
          <Stack.Screen name="RequestDetails" component={RequestDetailsScreen} />
          <Stack.Screen name="Evaluation" component={EvaluationScreen} />
        </>
      )}
    </Stack.Navigator>
  );
}
