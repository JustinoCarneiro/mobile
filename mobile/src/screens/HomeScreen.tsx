import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, ActivityIndicator, ScrollView, RefreshControl } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { CategoryItem, ProviderCard, Button } from '../components';
import { useAuthStore } from '../services/authStore';
import { useLocation } from '../hooks/useLocation';
import api from '../services/api';
import { NearbyProviderDTO } from '../types';

import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/types';

type NavigationProp = NativeStackNavigationProp<RootStackParamList, 'HomeTabs'>;

const CATEGORIES = [
  { id: 'Encanador', label: 'Encanador', icon: '🪠' },
  { id: 'Eletricista', label: 'Eletricista', icon: '⚡' },
  { id: 'Diarista', label: 'Diarista', icon: '🧹' },
  { id: 'Pintor', label: 'Pintor', icon: '🎨' },
  { id: 'Pedreiro', label: 'Pedreiro', icon: '🧱' },
];

export default function HomeScreen() {
  const navigation = useNavigation<NavigationProp>();
  const { clearAuth, role } = useAuthStore();
  const { coords, loading: loadingLocation, errorMsg: locationError } = useLocation();
  
  const [selectedCategory, setSelectedCategory] = useState(CATEGORIES[0].id);
  const [providers, setProviders] = useState<NearbyProviderDTO[]>([]);
  const [loadingProviders, setLoadingProviders] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const fetchProviders = async () => {
    if (!coords) return;

    setLoadingProviders(true);
    try {
      console.log(`[API] Buscando prestadores: cat=${selectedCategory}, lat=${coords.latitude}, lng=${coords.longitude}`);
      const response = await api.get<NearbyProviderDTO[]>('/providers/nearby', {
        params: {
          lat: coords.latitude,
          lng: coords.longitude,
          category: selectedCategory,
          radius: 20.0, // Raio de 20km por padrão
        },
      });
      setProviders(response.data);
    } catch (error: any) {
      console.error('[Error] Falha ao carregar prestadores:', error);
    } finally {
      setLoadingProviders(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    if (coords) {
      fetchProviders();
    }
  }, [coords, selectedCategory]);

  const onRefresh = () => {
    setRefreshing(true);
    fetchProviders();
  };

  const renderHeader = () => (
    <View className="px-6 pt-6 pb-2">
      <View className="flex-row justify-between items-center mb-6">
        <View>
          <Text className="text-gray-500 text-sm">Bem-vindo,</Text>
          <Text className="text-2xl font-bold text-gray-800">Ceará Services</Text>
        </View>
        <Button 
          title="Sair" 
          variant="secondary" 
          onPress={() => clearAuth()} 
          className="h-10 px-4"
        />
      </View>

      <Text className="text-lg font-bold text-gray-800 mb-4">O que você precisa hoje?</Text>
      
      <ScrollView 
        horizontal 
        showsHorizontalScrollIndicator={false} 
        className="flex-row mb-6"
        contentContainerStyle={{ paddingRight: 40 }}
      >
        {CATEGORIES.map((cat) => (
          <CategoryItem
            key={cat.id}
            label={cat.label}
            icon={cat.icon}
            isSelected={selectedCategory === cat.id}
            onPress={() => setSelectedCategory(cat.id)}
          />
        ))}
      </ScrollView>

      <Text className="text-lg font-bold text-gray-800 mb-4">
        {selectedCategory}s Próximos
      </Text>
    </View>
  );

  const renderEmpty = () => (
    <View className="flex-1 items-center justify-center py-20 px-10">
      <Text className="text-5xl mb-4">🔍</Text>
      <Text className="text-gray-800 text-lg font-bold text-center">Nenhum prestador encontrado</Text>
      <Text className="text-gray-500 text-center mt-2">
        Tente mudar a categoria ou verifique se há profissionais nesta região.
      </Text>
    </View>
  );

  return (
    <SafeAreaView className="flex-1 bg-gray-50">
      <FlatList
        data={providers}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View className="px-6">
            <ProviderCard 
              provider={item} 
              onPress={() => navigation.navigate('ServiceRequest', { 
                providerId: item.id, 
                providerName: item.fullName 
              })} 
            />
          </View>
        )}
        ListHeaderComponent={renderHeader}
        ListEmptyComponent={!loadingProviders ? renderEmpty : null}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#004595']} />
        }
        contentContainerStyle={{ paddingBottom: 40 }}
      />

      {(loadingLocation || (loadingProviders && !refreshing)) && (
        <View className="absolute inset-0 bg-white/60 items-center justify-center">
          <ActivityIndicator size="large" color="#004595" />
          <Text className="mt-4 text-blue-900 font-semibold">
            {loadingLocation ? 'Obtendo sua localização...' : 'Buscando prestadores...'}
          </Text>
        </View>
      )}

      {locationError && !coords && !loadingLocation && (
        <View className="absolute inset-x-6 bottom-10 bg-red-100 p-4 rounded-2xl border border-red-200">
          <Text className="text-red-800 font-bold mb-1">Erro de Localização</Text>
          <Text className="text-red-700 text-sm">{locationError}</Text>
        </View>
      )}
    </SafeAreaView>
  );
}
