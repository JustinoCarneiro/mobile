import React from 'react';
import { View, Text } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Button } from '../components/Button';
import { useAuthStore } from '../services/authStore';

export default function HomeScreen() {
  const { clearAuth, role } = useAuthStore();

  return (
    <SafeAreaView className="flex-1 bg-gray-50 items-center justify-center px-6">
      <View className="bg-white p-8 rounded-3xl shadow-sm w-full items-center">
        <Text className="text-4xl mb-4">🏠</Text>
        <Text className="text-2xl font-bold text-gray-800">Bem-vindo!</Text>
        <Text className="text-gray-500 mt-2 text-center">
          Você está logado como: {'\n'}
          <Text className="font-bold text-ceara-blue">{role}</Text>
        </Text>

        <Button 
          title="Sair" 
          variant="secondary" 
          onPress={() => clearAuth()} 
          className="mt-10 w-full"
        />
      </View>
    </SafeAreaView>
  );
}
