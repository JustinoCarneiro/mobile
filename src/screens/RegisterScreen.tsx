import React, { useState } from 'react';
import { View, Text, KeyboardAvoidingView, Platform, Alert, ScrollView, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Button } from '../components/Button';
import { Input } from '../components/Input';
import api from '../services/api';
import { UserRole } from '../types';

import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/types';

type NavigationProp = NativeStackNavigationProp<RootStackParamList, 'Register'>;

export default function RegisterScreen({ navigation }: { navigation: NavigationProp }) {
  
  const [role, setRole] = useState<UserRole>('ROLE_CLIENT');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [cpf, setCpf] = useState('');
  const [password, setPassword] = useState('');
  const [category, setCategory] = useState('');
  const [bio, setBio] = useState('');
  const [loading, setLoading] = useState(false);

  const handleRegister = async () => {
    if (!fullName || !email || !cpf || !password) {
      Alert.alert('Erro', 'Por favor, preencha todos os campos obrigatórios.');
      return;
    }

    if (role === 'ROLE_PROVIDER' && (!category || !bio)) {
      Alert.alert('Erro', 'Prestadores devem preencher Categoria e Bio.');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        fullName,
        email,
        cpf,
        password,
        role,
        category: role === 'ROLE_PROVIDER' ? category : undefined,
        bio: role === 'ROLE_PROVIDER' ? bio : undefined,
      };

      await api.post('/auth/register', payload);
      
      Alert.alert('Sucesso', 'Cadastro realizado! Agora você pode fazer login.', [
        { text: 'OK', onPress: () => navigation.goBack() }
      ]);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Erro ao realizar cadastro. Verifique os dados.';
      Alert.alert('Erro', message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: 'white' }}>
      <View className="flex-1 bg-white">
        <KeyboardAvoidingView 
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          className="flex-1"
        >
          <ScrollView contentContainerStyle={{ flexGrow: 1 }} className="px-6 py-10">
            <Text className="text-2xl font-bold text-gray-800 mb-2">Crie sua conta</Text>
            <Text className="text-gray-500 mb-8">Escolha seu perfil e preencha os dados</Text>

            {/* Seleção de Perfil */}
            <View className="flex-row mb-8 bg-gray-100 p-1 rounded-xl">
              <TouchableOpacity 
                onPress={() => setRole('ROLE_CLIENT')}
                className={`flex-1 flex-row h-10 items-center justify-center rounded-lg ${role === 'ROLE_CLIENT' ? 'bg-white shadow-sm' : ''}`}
              >
                <Text className={`font-semibold ${role === 'ROLE_CLIENT' ? 'text-ceara-blue' : 'text-gray-500'}`}>Sou Cliente</Text>
              </TouchableOpacity>
              <TouchableOpacity 
                onPress={() => setRole('ROLE_PROVIDER')}
                className={`flex-1 flex-row h-10 items-center justify-center rounded-lg ${role === 'ROLE_PROVIDER' ? 'bg-white shadow-sm' : ''}`}
              >
                <Text className={`font-semibold ${role === 'ROLE_PROVIDER' ? 'text-ceara-blue' : 'text-gray-500'}`}>Sou Prestador</Text>
              </TouchableOpacity>
            </View>

            <Input label="Nome Completo" placeholder="Ex: João Silva" value={fullName} onChangeText={setFullName} />
            <Input label="E-mail" placeholder="seu@email.com" value={email} onChangeText={setEmail} keyboardType="email-address" autoCapitalize="none" />
            <Input label="CPF (somente números)" placeholder="12345678900" value={cpf} onChangeText={setCpf} keyboardType="numeric" maxLength={11} />
            <Input label="Senha" placeholder="******" value={password} onChangeText={setPassword} secureTextEntry />

            {role === 'ROLE_PROVIDER' && (
              <View 
                style={{ 
                  marginTop: 8, 
                  padding: 16, 
                  backgroundColor: '#eff6ff', 
                  borderRadius: 12, 
                  borderWidth: 1, 
                  borderColor: '#dbeafe', 
                  marginBottom: 24 
                }}
              >
                <Text className="text-ceara-blue font-bold mb-4">Informações Profissionais</Text>
                <Input label="Categoria" placeholder="Ex: Eletricista" value={category} onChangeText={setCategory} />
                <Input label="Bio Curta" placeholder="Conte um pouco sobre sua experiência" value={bio} onChangeText={setBio} multiline numberOfLines={3} className="h-24" />
              </View>
            )}

            <Button title="Cadastrar" onPress={handleRegister} loading={loading} className="mt-4" />

            <TouchableOpacity onPress={() => navigation.goBack()} className="mt-6">
              <Text className="text-center text-gray-500">Já tem uma conta? <Text className="text-ceara-blue font-bold">Faça login</Text></Text>
            </TouchableOpacity>
          </ScrollView>
        </KeyboardAvoidingView>
      </View>
    </SafeAreaView>
  );
}
