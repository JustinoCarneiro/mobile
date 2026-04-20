import React, { useState, useEffect, useMemo } from 'react';
import { View, Text, FlatList, ActivityIndicator, RefreshControl, TouchableOpacity, Alert, Image } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import api from '../services/api';
import { ServiceRequestDTO, ServiceRequestStatus } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { Button } from '../components';
import { useAuthStore } from '../services/authStore';

type TabType = 'NEW' | 'ACTIVE';

export default function ProviderDashboardScreen() {
  const { clearAuth } = useAuthStore();
  const [requests, setRequests] = useState<ServiceRequestDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [activeTab, setActiveTab] = useState<TabType>('NEW');
  const [processingId, setProcessingId] = useState<string | null>(null);

  const fetchRequests = async () => {
    try {
      const response = await api.get<ServiceRequestDTO[]>('/services/provider/requests');
      setRequests(response.data);
    } catch (error) {
      console.error('[Error] Falha ao carregar pedidos do prestador:', error);
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

  const filteredRequests = useMemo(() => {
    if (activeTab === 'NEW') {
      return requests.filter(r => r.status === ServiceRequestStatus.PENDING);
    } else {
      return requests.filter(r => 
        r.status === ServiceRequestStatus.ACCEPTED || 
        r.status === ServiceRequestStatus.PAYMENT_CONFIRMED
      );
    }
  }, [requests, activeTab]);

  const handleUpdateStatus = async (requestId: string, nextStatus: ServiceRequestStatus) => {
    setProcessingId(requestId);
    try {
      await api.patch(`/services/${requestId}/status`, { status: nextStatus });
      Alert.alert('Sucesso', 'Status do pedido atualizado!');
      fetchRequests();
    } catch (error: any) {
      console.error('[Error] Falha ao atualizar status:', error);
      Alert.alert('Erro', 'Não foi possível atualizar o status.');
    } finally {
      setProcessingId(null);
    }
  };

  const renderActionButton = (request: ServiceRequestDTO) => {
    if (processingId === request.id) {
      return <ActivityIndicator size="small" color="#004595" />;
    }

    switch (request.status) {
      case ServiceRequestStatus.PENDING:
        return (
          <Button 
            title="Aceitar Pedido" 
            onPress={() => handleUpdateStatus(request.id, ServiceRequestStatus.ACCEPTED)}
            className="h-10"
          />
        );
      case ServiceRequestStatus.ACCEPTED:
        return (
          <Button 
            title="Iniciar Trabalho" 
            onPress={() => handleUpdateStatus(request.id, ServiceRequestStatus.PAYMENT_CONFIRMED)}
            variant="secondary"
            className="h-10"
          />
        );
      case ServiceRequestStatus.PAYMENT_CONFIRMED:
        return (
          <Button 
            title="Marcar como Concluído" 
            onPress={() => handleUpdateStatus(request.id, ServiceRequestStatus.COMPLETED)}
            className="h-10 bg-green-600 border-green-600"
          />
        );
      default:
        return null;
    }
  };

  const renderItem = ({ item }: { item: ServiceRequestDTO }) => (
    <View className="bg-white p-5 rounded-3xl mb-4 border border-gray-100 shadow-sm">
      <View className="flex-row justify-between items-start mb-3">
        <View className="flex-1 mr-2">
          <Text className="text-gray-500 text-xs font-bold uppercase mb-1">Cliente</Text>
          <Text className="text-lg font-bold text-gray-800">{item.client.fullName}</Text>
        </View>
        <StatusBadge status={item.status} />
      </View>

      <Text className="text-gray-600 mb-4" numberOfLines={3}>{item.description}</Text>

      {item.mediaUrl && (
        <Image 
          source={{ uri: item.mediaUrl }} 
          className="w-full h-32 rounded-2xl mb-4 bg-gray-50"
          resizeMode="cover"
        />
      )}

      <View className="pt-3 border-t border-gray-50">
        {renderActionButton(item)}
      </View>
    </View>
  );

  return (
    <SafeAreaView className="flex-1 bg-gray-50">
      <View className="px-6 pt-6 pb-2 flex-row justify-between items-center">
        <View>
          <Text className="text-2xl font-bold text-gray-800">Meu Painel</Text>
          <Text className="text-gray-500">Gerencie seus atendimentos</Text>
        </View>
        <TouchableOpacity onPress={clearAuth} className="p-2 bg-gray-100 rounded-full">
          <Text>🚪</Text>
        </TouchableOpacity>
      </View>

      {/* Tabs */}
      <View className="flex-row px-6 mt-4 mb-4">
        <TouchableOpacity 
          onPress={() => setActiveTab('NEW')}
          className={`flex-1 py-3 border-b-2 items-center ${activeTab === 'NEW' ? 'border-blue-800' : 'border-transparent'}`}
        >
          <Text className={`font-bold ${activeTab === 'NEW' ? 'text-blue-800' : 'text-gray-400'}`}>Novos</Text>
        </TouchableOpacity>
        <TouchableOpacity 
          onPress={() => setActiveTab('ACTIVE')}
          className={`flex-1 py-3 border-b-2 items-center ${activeTab === 'ACTIVE' ? 'border-blue-800' : 'border-transparent'}`}
        >
          <Text className={`font-bold ${activeTab === 'ACTIVE' ? 'text-blue-800' : 'text-gray-400'}`}>Ativos</Text>
        </TouchableOpacity>
      </View>

      {loading && !refreshing ? (
        <View className="flex-1 items-center justify-center">
          <ActivityIndicator size="large" color="#004595" />
        </View>
      ) : (
        <FlatList
          data={filteredRequests}
          keyExtractor={(item) => item.id}
          renderItem={renderItem}
          ListEmptyComponent={
            <View className="flex-1 items-center justify-center py-20 px-10">
              <Text className="text-5xl mb-4">{activeTab === 'NEW' ? '📭' : '🛠️'}</Text>
              <Text className="text-gray-800 text-lg font-bold text-center">Tudo limpo por aqui</Text>
              <Text className="text-gray-500 text-center mt-2">
                {activeTab === 'NEW' ? 'Nenhuma nova solicitação no momento.' : 'Você não tem serviços em andamento.'}
              </Text>
            </View>
          }
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#004595']} />
          }
          contentContainerStyle={{ paddingHorizontal: 24, paddingBottom: 40 }}
        />
      )}
    </SafeAreaView>
  );
}
