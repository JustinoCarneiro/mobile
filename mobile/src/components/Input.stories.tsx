import React from 'react';
import { View } from 'react-native';
import { Input } from './Input';

export default {
  title: 'Components/Input',
  component: Input,
  decorators: [
    (Story: any) => (
      <View style={{ padding: 20, flex: 1, justifyContent: 'center' }}>
        <Story />
      </View>
    ),
  ],
};

export const Default = {
  args: {
    label: 'Nome Completo',
    placeholder: 'Ex: João da Silva',
  },
};

export const WithIcon = {
  args: {
    label: 'E-mail',
    placeholder: 'seu@email.com',
    icon: '📧',
  },
};

export const WithError = {
  args: {
    label: 'Senha',
    placeholder: '******',
    error: 'A senha deve ter pelo menos 6 caracteres',
    isPassword: true,
  },
};

export const Disabled = {
  args: {
    label: 'Campo Bloqueado',
    value: 'Valor fixo',
    editable: false,
  },
};
