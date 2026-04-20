import React, { useState } from 'react';
import { View, Text, TextInput, TextInputProps } from 'react-native';

/**
 * Componente Input — Design System Marketplace Ceará.
 *
 * Props (além de todas as props padrão do TextInput):
 *   label      — rótulo exibido acima do campo
 *   error      — mensagem de erro (borda vermelha + texto abaixo)
 *   icon       — emoji ou caractere exibido à esquerda dentro do campo
 *   isPassword — atalho para secureTextEntry
 *   className  — classes NativeWind adicionais no container
 *
 * Comportamento:
 *   - Borda muda para azul ao focar, vermelho se houver erro.
 *   - Mensagem de erro aparece em vermelho abaixo do campo.
 *
 * ⚠️  NÃO usar classes shadow-* do NativeWind — consulte 2_regras.md.
 */
interface InputProps extends TextInputProps {
  label: string;
  error?: string;
  icon?: string;
  isPassword?: boolean;
  className?: string;
}

export const Input = ({
  label,
  error,
  icon,
  isPassword,
  className = '',
  secureTextEntry,
  ...props
}: InputProps) => {
  const [isFocused, setIsFocused] = useState(false);

  // Determina a cor da borda: erro > foco > padrão  
  const borderColor = error
    ? 'border-red-500'
    : isFocused
      ? 'border-blue-800'
      : 'border-gray-200';

  return (
    <View className={`mb-4 ${className}`}>
      <Text className="text-gray-700 font-semibold mb-1 ml-1">{label}</Text>

      <View
        className={`flex-row items-center bg-gray-100 h-12 rounded-xl border ${borderColor}`}
      >
        {icon && (
          <Text className="pl-4 text-base text-gray-400">{icon}</Text>
        )}

        <TextInput
          placeholderTextColor="#9ca3af"
          secureTextEntry={isPassword ?? secureTextEntry}
          onFocus={(e) => {
            setIsFocused(true);
            props.onFocus?.(e);
          }}
          onBlur={(e) => {
            setIsFocused(false);
            props.onBlur?.(e);
          }}
          className={`flex-1 h-12 text-gray-900 ${icon ? 'pl-2' : 'pl-4'} pr-4`}
          {...props}
        />
      </View>

      {error && (
        <Text className="text-red-500 text-xs mt-1 ml-1">{error}</Text>
      )}
    </View>
  );
};
