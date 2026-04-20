import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, Image, TouchableOpacity, Alert, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/types';
import { Button } from '../components';
import { StatusBadge } from '../components/StatusBadge';
import api from '../services/api';
import { ServiceRequestDTO, ServiceRequestStatus } from '../types';

type Props = NativeStackScreenProps<RootStackParamList, 'RequestDetails'>;

export default function RequestDetailsScreen({ route, navigation }: Props) {
  const { requestId } = route.params;

  const [request, setRequest] = useState<ServiceRequestDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [releasingPayment, setReleasingPayment] = useState(false);

  const fetchRequestDetails = async () => {
    try {
      const response = await api.get<ServiceRequestDTO>(`/services/requests/${requestId}`);
      setRequest(response.data);
    } catch (error) {
      console.error('[Error] Falha ao carregar detalhes do pedido:', error);
      Alert.alert('Erro', 'Não foi possível carregar os detalhes do pedido.');
      navigation.goBack();
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequestDetails();
  }, [requestId]);

  const handleReleasePayment = async () => {
    Alert.alert(
      'Confirmar Liberação',
      'Você confirma que o serviço foi entregue conforme o combinado? Após a liberação, o dinheiro será enviado ao prestador e não poderá ser estornado.',
      [
        { text: 'Cancelar', style: 'cancel' },
        { 
          text: 'Sim, Liberar Pagamento', 
          onPress: async () => {
            setReleasingPayment(true);
            try {
              await api.post(`/payments/release/${requestId}`);
              Alert.alert('Sucesso', 'Pagamento liberado com sucesso!', [
                { text: 'Avaliar Prestador', onPress: () => navigation.navigate('Evaluation', { requestId }) }
              ]);
              fetchRequestDetails(); // Atualizar status na tela
            } catch (error: any) {
              console.error('[Error] Falha ao liberar pagamento:', error);
              const msg = error.response?.data?.message || 'Falha na transação. Tente novamente.';
              Alert.alert('Erro', msg);
            } finally {
              setReleasingPayment(false);
            }
          }
        }
      ]
    );
  };

  if (loading) {
    return (
      <View className="flex-1 bg-white items-center justify-center">
        <ActivityIndicator size="large" color="#004595" />
      </View>
    );
  }

  if (!request) return null;

  return (
    <SafeAreaView className="flex-1 bg-white">
      <ScrollView className="flex-1" contentContainerStyle={{ paddingBottom: 40 }}>
        {/* Header */}
        <View className="px-6 py-4 flex-row items-center border-b border-gray-50">
          <TouchableOpacity onPress={() => navigation.goBack()} className="mr-4">
            <Text className="text-blue-800 font-bold text-lg">←</Text>
          </TouchableOpacity>
          <Text className="text-xl font-bold text-gray-800">Detalhes do Pedido</Text>
        </View>

        <View className="p-6">
          {/* Status e Prestador */}
          <View className="flex-row justify-between items-start mb-6">
            <View className="flex-1 mr-4">
              <Text className="text-gray-500 text-xs uppercase font-bold mb-1">Prestador</Text>
              <Text className="text-2xl font-bold text-gray-800">{request.provider.fullName}</Text>
              <Text className="text-gray-500">{request.provider.category}</Text>
            </View>
            <StatusBadge status={request.status} />
          </View>

          {/* Descrição */}
          <View className="mb-8">
            <Text className="text-gray-800 font-bold mb-2">Descrição do Problema</Text>
            <View className="bg-gray-50 p-4 rounded-2xl">
              <Text className="text-gray-600 leading-6">{request.description}</Text>
            </View>
          </View>

          {/* Mídia (Se houver) */}
          {request.mediaUrl && (
            <View className="mb-8">
              <Text className="text-gray-800 font-bold mb-3">Evidência Anexada</Text>
              <Image 
                source={{ uri: request.mediaUrl }} 
                className="w-full h-64 rounded-2xl bg-gray-100"
                resizeMode="cover"
              />
            </View>
          )}

          {/* Seção Financeira (Escrow) */}
          <View className="bg-blue-50 p-6 rounded-3xl border border-blue-100 mb-8">
            <Text className="text-blue-900 font-bold mb-2">💰 Informações do Pagamento</Text>
            
            {request.status === ServiceRequestStatus.PAYMENT_CONFIRMED && (
              <Text className="text-blue-700 italic">
                O valor está garantido pela plataforma e será liberado assim que o prestador concluir o serviço.
              </Text>
            )}

            {request.status === ServiceRequestStatus.COMPLETED && (
              <View>
                <Text className="text-blue-700 mb-4">
                  O prestador marcou o serviço como **Concluído**. Revise o trabalho e libere o pagamento se estiver tudo certo.
                </Text>
                <Button 
                  title="Confirmar e Liberar Pagamento" 
                  onPress={handleReleasePayment}
                  isLoading={releasingPayment}
                />
              </View>
            )}

            {request.status === ServiceRequestStatus.PAYMENT_CONFIRMED === false && 
             request.status === ServiceRequestStatus.COMPLETED === false && (
              <Text className="text-gray-500 italic">
                Aguardando aprovação do prestador para iniciar o fluxo de pagamento.
              </Text>
            )}
          </View>

          <Text className="text-center text-gray-400 text-xs">
            Pedido realizado em {new Date(request.createdAt).toLocaleDateString('pt-BR')} às {new Date(request.createdAt).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}
          </Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
