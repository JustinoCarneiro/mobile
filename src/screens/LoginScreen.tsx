import React, { useState } from 'react';
import { View, Text, KeyboardAvoidingView, Platform, Alert, ScrollView, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/types';
import { Button, Input } from '../components';
import api from '../services/api';
import { useAuthStore } from '../services/authStore';
import { AuthResponse } from '../types';

type NavigationProp = NativeStackNavigationProp<RootStackParamList, 'Login'>;

export default function LoginScreen({ navigation }: { navigation: NavigationProp }) {
  const { setAuth } = useAuthStore();
  
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert('Erro', 'Por favor, preencha todos os campos.');
      return;
    }

    setLoading(true);
    try {
      const response = await api.post<AuthResponse>('/auth/login', { email, password });
      
      console.log('[Auth] Login realizado com sucesso. Token:', response.data.token);
      
      setAuth(response.data.token, response.data.role);
      // O AppNavigator mudará automaticamente para a Home devido à mudança no token
    } catch (error: any) {
      const message = error.response?.data?.message || 'Erro ao realizar login. Verifique suas credenciais.';
      Alert.alert('Erro', message);
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
        <ScrollView contentContainerStyle={{ flexGrow: 1 }} className="px-6 pt-12">
          {/* Logo */}
          <View className="items-center mb-10">
            <Text className="text-3xl font-bold text-ceara-blue">Marketplace</Text>
            <Text className="text-gray-500 mt-2 text-lg">Ceará Services</Text>
          </View>

          {/* Cabeçalho */}
          <View className="mb-6">
            <Text className="text-2xl font-bold text-gray-800 mb-2">Bem-vindo</Text>
            <Text className="text-gray-500">Faça login para continuar</Text>
          </View>

          {/* Campos */}
          <Input
            label="E-mail"
            icon="✉️"
            placeholder="seu@email.com"
            value={email}
            onChangeText={setEmail}
            keyboardType="email-address"
            autoCapitalize="none"
          />

          <Input
            label="Senha"
            icon="🔒"
            placeholder="******"
            value={password}
            onChangeText={setPassword}
            isPassword
          />

          {/* Botão de Login */}
          <Button 
            title="Entrar" 
            onPress={handleLogin} 
            isLoading={loading}
            className="mt-4"
          />

          {/* Link para Cadastro */}
          <View className="flex-row justify-center mt-8">
            <Text className="text-gray-600">Não tem uma conta? </Text>
            <TouchableOpacity onPress={() => navigation.navigate({ name: 'Register', params: {} })}>
              <Text className="text-ceara-blue font-bold">Cadastre-se</Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
