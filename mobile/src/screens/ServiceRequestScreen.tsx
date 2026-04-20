import React, { useState } from 'react';
import { View, Text, ScrollView, Image, TouchableOpacity, Alert, KeyboardAvoidingView, Platform } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import * as ImagePicker from 'expo-image-picker';
import { RootStackParamList } from '../navigation/types';
import { Button, Input } from '../components';
import api from '../services/api';

type Props = NativeStackScreenProps<RootStackParamList, 'ServiceRequest'>;

export default function ServiceRequestScreen({ route, navigation }: Props) {
  const { providerId, providerName } = route.params;

  const [description, setDescription] = useState('');
  const [image, setImage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const pickImage = async () => {
    // Solicitar permissão de galeria
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
    
    if (status !== 'granted') {
      Alert.alert('Permissão Necessária', 'Precisamos de acesso às suas fotos para anexar a imagem do problema.');
      return;
    }

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ['images'],
      allowsEditing: true,
      aspect: [4, 3],
      quality: 0.7,
    });

    if (!result.canceled) {
      setImage(result.assets[0].uri);
    }
  };

  const handleSendRequest = async () => {
    if (!description.trim()) {
      Alert.alert('Erro', 'Por favor, descreva o problema.');
      return;
    }

    setLoading(true);

    try {
      const formData = new FormData();
      formData.append('description', description);
      formData.append('providerId', providerId);

      if (image) {
        const localUri = image;
        const filename = localUri.split('/').pop() || 'image.jpg';
        const match = /\.(\w+)$/.exec(filename);
        const type = match ? `image/${match[1]}` : `image`;

        // @ts-ignore
        formData.append('file', { uri: localUri, name: filename, type });
      }

      await api.post('/services/requests', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      Alert.alert('Sucesso', 'Sua solicitação foi enviada com sucesso! O prestador entrará em contato em breve.', [
        { text: 'OK', onPress: () => navigation.navigate('HomeTabs', { screen: 'Pedidos' }) }
      ]);
    } catch (error: any) {
      console.error('[Error] Falha ao enviar solicitação:', error);
      const msg = error.response?.data?.message || 'Não foi possível enviar sua solicitação. Tente novamente mais tarde.';
      Alert.alert('Erro', msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView className="flex-1 bg-white">
      <KeyboardAvoidingView 
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        className="flex-1"
      >
        <ScrollView contentContainerStyle={{ flexGrow: 1 }} className="px-6 py-6">
          <View className="mb-8">
            <Text className="text-gray-500 text-sm uppercase font-bold mb-1">Solicitando para</Text>
            <Text className="text-2xl font-bold text-gray-800">{providerName}</Text>
          </View>

          <Input
            label="Descreva o seu problema"
            placeholder="Ex: Preciso consertar um vazamento na pia da cozinha que começou hoje de manhã..."
            value={description}
            onChangeText={setDescription}
            multiline
            numberOfLines={5}
            textAlignVertical="top"
            className="mb-6 h-32"
          />

          <Text className="text-gray-700 font-semibold mb-2 ml-1">Anexar uma foto (Opcional)</Text>
          
          <TouchableOpacity 
            onPress={pickImage}
            activeOpacity={0.7}
            className="w-full h-48 bg-gray-50 rounded-2xl border-2 border-dashed border-gray-200 items-center justify-center overflow-hidden mb-10"
          >
            {image ? (
              <View className="w-full h-full relative">
                <Image source={{ uri: image }} className="w-full h-full" resizeMode="cover" />
                <View className="absolute top-2 right-2 bg-black/50 px-3 py-1 rounded-full">
                  <Text className="text-white text-xs font-bold">Trocar Foto</Text>
                </View>
              </View>
            ) : (
              <View className="items-center">
                <Text className="text-4xl mb-2">📸</Text>
                <Text className="text-gray-400">Clique para selecionar da galeria</Text>
              </View>
            )}
          </TouchableOpacity>

          <View className="mt-auto">
            <Button 
              title="Enviar Solicitação" 
              onPress={handleSendRequest} 
              isLoading={loading}
            />
            <TouchableOpacity 
              onPress={() => navigation.goBack()}
              className="mt-4 py-2"
            >
              <Text className="text-center text-gray-400 font-semibold">Cancelar</Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
