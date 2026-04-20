import React, { useState } from 'react';
import { View, Text, KeyboardAvoidingView, Platform, Alert, ScrollView, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { RootStackParamList } from '../navigation/types';
import { Button, Input } from '../components';
import api from '../services/api';
import { UserRole } from '../types';

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
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!fullName.trim()) newErrors.fullName = 'Nome é obrigatório';
    if (!email.trim()) newErrors.email = 'E-mail é obrigatório';
    if (!cpf.trim()) newErrors.cpf = 'CPF é obrigatório';
    else if (cpf.length !== 11) newErrors.cpf = 'CPF deve ter 11 dígitos';
    if (!password.trim()) newErrors.password = 'Senha é obrigatória';
    else if (password.length < 6) newErrors.password = 'Mínimo de 6 caracteres';

    if (role === 'ROLE_PROVIDER') {
      if (!category.trim()) newErrors.category = 'Categoria é obrigatória para prestadores';
      if (!bio.trim()) newErrors.bio = 'Bio é obrigatória para prestadores';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async () => {
    if (!validate()) return;

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
    <SafeAreaView className="flex-1 bg-white">
      <KeyboardAvoidingView 
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        className="flex-1"
      >
        <ScrollView contentContainerStyle={{ flexGrow: 1 }} className="px-6 py-10">
          {/* Cabeçalho */}
          <Text className="text-2xl font-bold text-gray-800 mb-2">Crie sua conta</Text>
          <Text className="text-gray-500 mb-8">Escolha seu perfil e preencha os dados</Text>

          {/* Seleção de Perfil */}
          <View className="flex-row mb-8 bg-gray-100 p-1 rounded-xl">
            <TouchableOpacity 
              onPress={() => setRole('ROLE_CLIENT')}
              className={`flex-1 flex-row h-10 items-center justify-center rounded-lg ${role === 'ROLE_CLIENT' ? 'bg-white' : ''}`}
              style={role === 'ROLE_CLIENT' ? { shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.05, shadowRadius: 2, elevation: 1 } : undefined}
            >
              <Text className={`font-semibold ${role === 'ROLE_CLIENT' ? 'text-ceara-blue' : 'text-gray-500'}`}>Sou Cliente</Text>
            </TouchableOpacity>
            <TouchableOpacity 
              onPress={() => setRole('ROLE_PROVIDER')}
              className={`flex-1 flex-row h-10 items-center justify-center rounded-lg ${role === 'ROLE_PROVIDER' ? 'bg-white' : ''}`}
              style={role === 'ROLE_PROVIDER' ? { shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.05, shadowRadius: 2, elevation: 1 } : undefined}
            >
              <Text className={`font-semibold ${role === 'ROLE_PROVIDER' ? 'text-ceara-blue' : 'text-gray-500'}`}>Sou Prestador</Text>
            </TouchableOpacity>
          </View>

          {/* Campos Obrigatórios */}
          <Input
            label="Nome Completo"
            icon="👤"
            placeholder="Ex: João Silva"
            value={fullName}
            onChangeText={(v) => { setFullName(v); setErrors((e) => ({ ...e, fullName: '' })); }}
            error={errors.fullName}
          />

          <Input
            label="E-mail"
            icon="✉️"
            placeholder="seu@email.com"
            value={email}
            onChangeText={(v) => { setEmail(v); setErrors((e) => ({ ...e, email: '' })); }}
            keyboardType="email-address"
            autoCapitalize="none"
            error={errors.email}
          />

          <Input
            label="CPF (somente números)"
            icon="🪪"
            placeholder="12345678900"
            value={cpf}
            onChangeText={(v) => { setCpf(v); setErrors((e) => ({ ...e, cpf: '' })); }}
            keyboardType="numeric"
            maxLength={11}
            error={errors.cpf}
          />

          <Input
            label="Senha"
            icon="🔒"
            placeholder="Mínimo 6 caracteres"
            value={password}
            onChangeText={(v) => { setPassword(v); setErrors((e) => ({ ...e, password: '' })); }}
            isPassword
            error={errors.password}
          />

          {/* Campos de Prestador */}
          {role === 'ROLE_PROVIDER' && (
            <View className="mt-2 p-4 bg-blue-50 rounded-xl border border-blue-100 mb-6">
              <Text className="text-ceara-blue font-bold mb-4">Informações Profissionais</Text>
              
              <Input
                label="Categoria"
                icon="🔧"
                placeholder="Ex: Eletricista"
                value={category}
                onChangeText={(v) => { setCategory(v); setErrors((e) => ({ ...e, category: '' })); }}
                error={errors.category}
              />

              <Input
                label="Bio Curta"
                icon="📝"
                placeholder="Conte um pouco sobre sua experiência"
                value={bio}
                onChangeText={(v) => { setBio(v); setErrors((e) => ({ ...e, bio: '' })); }}
                multiline
                numberOfLines={3}
                error={errors.bio}
              />
            </View>
          )}

          {/* Botão de Cadastro */}
          <Button
            title="Cadastrar"
            onPress={handleRegister}
            isLoading={loading}
            className="mt-4"
          />

          {/* Link para Login */}
          <TouchableOpacity onPress={() => navigation.goBack()} className="mt-6 mb-8">
            <Text className="text-center text-gray-500">
              Já tem uma conta? <Text className="text-ceara-blue font-bold">Faça login</Text>
            </Text>
          </TouchableOpacity>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
