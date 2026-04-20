import React from 'react';
import { TouchableOpacity, Text, ActivityIndicator } from 'react-native';

/**
 * Componente Button — Design System Marketplace Ceará.
 *
 * Props:
 *   title      — texto do botão
 *   onPress    — callback ao tocar
 *   isLoading  — exibe ActivityIndicator branco em vez do texto
 *   loading    — alias de isLoading (retrocompatibilidade)
 *   disabled   — desabilita interação e aplica opacidade reduzida
 *   variant    — 'primary' (bg-blue-800) | 'secondary' (bg-gray-200)
 *   className  — classes NativeWind adicionais
 *
 * ⚠️  NÃO usar classes shadow-* do NativeWind — consulte 2_regras.md.
 */
interface ButtonProps {
  title: string;
  onPress: () => void;
  isLoading?: boolean;
  /** @deprecated Use isLoading */
  loading?: boolean;
  disabled?: boolean;
  variant?: 'primary' | 'secondary';
  className?: string;
}

export const Button = ({
  title,
  onPress,
  isLoading,
  loading,
  disabled = false,
  variant = 'primary',
  className = '',
}: ButtonProps) => {
  const showLoader = isLoading ?? loading ?? false;
  const isDisabled = disabled || showLoader;

  const bg = variant === 'primary' ? 'bg-blue-800' : 'bg-gray-200';
  const textColor = variant === 'primary' ? 'text-white' : 'text-gray-800';
  const indicatorColor = variant === 'primary' ? '#ffffff' : '#1f2937';

  return (
    <TouchableOpacity
      activeOpacity={0.7}
      disabled={isDisabled}
      onPress={onPress}
      className={`${bg} h-12 rounded-xl flex-row items-center justify-center px-4 ${className}`}
      style={isDisabled ? { opacity: 0.55 } : undefined}
    >
      {showLoader ? (
        <ActivityIndicator color={indicatorColor} />
      ) : (
        <Text className={`${textColor} font-bold text-base`}>{title}</Text>
      )}
    </TouchableOpacity>
  );
};
