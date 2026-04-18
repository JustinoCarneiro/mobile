import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useAuthStore } from '../services/authStore';

export default function ClientHomeScreen() {
  const { clearAuth } = useAuthStore();

  return (
    <View className="flex-1 bg-white items-center justify-center p-4">
      <Text className="text-2xl font-bold text-ceara-blue mb-4">Marketplace Ceará</Text>
      <Text className="text-gray-600 mb-8 text-center">Encontre os melhores serviços para você!</Text>
      
      <TouchableOpacity 
        className="bg-red-500 p-4 rounded-xl w-full"
        onPress={clearAuth}
      >
        <Text className="text-white text-center font-bold">Sair</Text>
      </TouchableOpacity>
    </View>
  );
}
