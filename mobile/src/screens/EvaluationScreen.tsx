import React, { useState } from 'react';
import { View, Text, TouchableOpacity, Alert, KeyboardAvoidingView, Platform, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { CommonActions } from '@react-navigation/native';
import { RootStackParamList } from '../navigation/types';
import { Button, Input } from '../components';
import api from '../services/api';

type Props = NativeStackScreenProps<RootStackParamList, 'Evaluation'>;

export default function EvaluationScreen({ route, navigation }: Props) {
  const { requestId } = route.params;

  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSendReview = async () => {
    if (rating === 0) {
      Alert.alert('Atenção', 'Por favor, selecione uma nota de 1 a 5 estrelas.');
      return;
    }

    setLoading(true);

    try {
      await api.post(`/services/${requestId}/reviews`, {
        rating,
        comment: comment.trim() || null,
      });

      Alert.alert('Obrigado!', 'Sua avaliação foi enviada com sucesso.', [
        {
          text: 'OK',
          onPress: () => {
            // Resetar a navegação para a Home limpando o histórico
            navigation.dispatch(
              CommonActions.reset({
                index: 0,
                routes: [{ name: 'HomeTabs' }],
              })
            );
          },
        },
      ]);
    } catch (error: any) {
      console.error('[Error] Falha ao enviar avaliação:', error);
      
      // Tratamento de erro para duplicidade ou outros problemas
      const msg = error.response?.data?.message || 'Não foi possível enviar sua avaliação no momento.';
      Alert.alert('Erro', msg);

      if (error.response?.status === 400 || error.response?.status === 409) {
        // Se já foi avaliado ou erro de negócio, volta pra home
        navigation.navigate('HomeTabs', { screen: 'Explorar' });
      }
    } finally {
      setLoading(false);
    }
  };

  const renderStars = () => {
    return (
      <View className="flex-row justify-between w-full px-4 mb-8">
        {[1, 2, 3, 4, 5].map((star) => (
          <TouchableOpacity
            key={star}
            onPress={() => setRating(star)}
            activeOpacity={0.7}
            className="p-2"
          >
            <Text className={`text-4xl ${rating >= star ? 'text-yellow-400' : 'text-gray-200'}`}>
              ★
            </Text>
          </TouchableOpacity>
        ))}
      </View>
    );
  };

  return (
    <SafeAreaView className="flex-1 bg-white">
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        className="flex-1"
      >
        <ScrollView contentContainerStyle={{ flexGrow: 1 }} className="px-6 py-10">
          <View className="items-center mb-10">
            <View className="w-20 h-20 bg-blue-50 rounded-full items-center justify-center mb-4">
              <Text className="text-4xl">🏆</Text>
            </View>
            <Text className="text-2xl font-bold text-gray-800 text-center">Como foi sua experiência?</Text>
            <Text className="text-gray-500 text-center mt-2 px-6">
              Sua avaliação ajuda outros clientes e valoriza o trabalho dos prestadores.
            </Text>
          </View>

          <Text className="text-center text-gray-700 font-bold mb-4 uppercase text-xs tracking-widest">
            Sua Nota
          </Text>
          {renderStars()}

          <Input
            label="Conte-nos mais (Opcional)"
            placeholder="O que você achou da pontualidade, qualidade e educação do prestador?"
            value={comment}
            onChangeText={setComment}
            multiline
            numberOfLines={4}
            textAlignVertical="top"
            className="mb-8 h-28"
          />

          <View className="mt-auto">
            <Button
              title="Finalizar Avaliação"
              onPress={handleSendReview}
              isLoading={loading}
              disabled={rating === 0}
            />
            
            <TouchableOpacity
              onPress={() => navigation.dispatch(
                CommonActions.reset({
                  index: 0,
                  routes: [{ name: 'HomeTabs' }],
                })
              )}
              disabled={loading}
              className="mt-6 p-2"
            >
              <Text className="text-center text-gray-400 font-semibold">Pular Avaliação</Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
