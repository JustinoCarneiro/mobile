import React from 'react';
import { View } from 'react-native';
import { Button } from './Button';

export default {
  title: 'Components/Button',
  component: Button,
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
    title: 'Clique Aqui',
    onPress: () => console.log('Pressed'),
  },
};

export const Loading = {
  args: {
    title: 'Carregando...',
    isLoading: true,
  },
};

export const Disabled = {
  args: {
    title: 'Desabilitado',
    disabled: true,
  },
};

export const Secondary = {
  args: {
    title: 'Botão Secundário',
    variant: 'secondary',
  },
};
export const Error = {
  args: {
    title: 'Erro na Operação',
    className: 'bg-red-600',
  },
};
