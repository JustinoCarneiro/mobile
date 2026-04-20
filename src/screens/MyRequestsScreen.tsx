import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, ActivityIndicator, RefreshControl, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/types';
import api from '../services/api';
import { ServiceRequestDTO, ServiceRequestStatus } from '../types';

import { StatusBadge } from '../components/StatusBadge';

type NavigationProp = NativeStackNavigationProp<RootStackParamList, 'HomeTabs'>;

const RequestCard = ({ request, onPress }: { request: ServiceRequestDTO; onPress: () => void }) => {
  return (
    <TouchableOpacity
      onPress={onPress}
      activeOpacity={0.7}
      className="bg-white p-4 rounded-2xl mb-4 border border-gray-100 shadow-sm"
      style={{
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.05,
        shadowRadius: 5,
        elevation: 1,
      }}
    >
      <View className="flex-row justify-between items-start mb-2">
        <View className="flex-1 mr-2">
          <Text className="text-lg font-bold text-gray-800" numberOfLines={1}>
            {request.provider.fullName}
          </Text>
          <Text className="text-gray-500 text-sm">{request.provider.category}</Text>
        </View>
        <StatusBadge status={request.status} />
      </View>

      <Text className="text-gray-600 text-sm mb-3" numberOfLines={2}>
        {request.description}
      </Text>

      <View className="flex-row justify-between items-center pt-2 border-t border-gray-50">
        <Text className="text-gray-400 text-xs">
          Solicitado em: {new Date(request.createdAt).toLocaleDateString('pt-BR')}
        </Text>
        <Text className="text-blue-800 font-bold text-xs">Ver detalhes ➔</Text>
      </View>
    </TouchableOpacity>
  );
};

export default function MyRequestsScreen() {
  const navigation = useNavigation<NavigationProp>();
  const [requests, setRequests] = useState<ServiceRequestDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchRequests = async () => {
    try {
      const response = await api.get<ServiceRequestDTO[]>('/services/requests/my-requests');
      setRequests(response.data);
    } catch (error) {
      console.error('[Error] Falha ao carregar pedidos:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const onRefresh = () => {
    setRefreshing(true);
    fetchRequests();
  };

  const renderEmpty = () => (
    <View className="flex-1 items-center justify-center py-20 px-10">
      <Text className="text-5xl mb-4">📝</Text>
      <Text className="text-gray-800 text-lg font-bold text-center">Nenhum pedido ainda</Text>
      <Text className="text-gray-500 text-center mt-2">
        Suas solicitações de serviço aparecerão aqui assim que você fizer a primeira busca.
      </Text>
    </View>
  );

  return (
    <SafeAreaView className="flex-1 bg-gray-50">
      <View className="px-6 pt-6 pb-4">
        <Text className="text-2xl font-bold text-gray-800">Meus Pedidos</Text>
        <Text className="text-gray-500">Acompanhe o status dos seus chamados</Text>
      </View>

      {loading && !refreshing ? (
        <View className="flex-1 items-center justify-center">
          <ActivityIndicator size="large" color="#004595" />
        </View>
      ) : (
        <FlatList
          data={requests}
          keyExtractor={(item) => item.id}
          renderItem={({ item }) => (
            <View className="px-6">
              <RequestCard 
                request={item} 
                onPress={() => navigation.navigate('RequestDetails', { requestId: item.id })} 
              />
            </View>
          )}
          ListEmptyComponent={renderEmpty}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#004595']} />
          }
          contentContainerStyle={{ paddingBottom: 40 }}
        />
      )}
    </SafeAreaView>
  );
}
