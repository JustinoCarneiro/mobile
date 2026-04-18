import React from 'react';
import { TouchableOpacity, Text, ActivityIndicator } from 'react-native';

interface ButtonProps {
  title: string;
  onPress: () => void;
  loading?: boolean;
  variant?: 'primary' | 'secondary';
  className?: string;
}

export const Button = ({ title, onPress, loading, variant = 'primary', className = '' }: ButtonProps) => {
  const bg = variant === 'primary' ? 'bg-ceara-blue' : 'bg-gray-200';
  const text = variant === 'primary' ? 'text-white' : 'text-gray-800';

  return (
    <TouchableOpacity
      activeOpacity={0.7}
      disabled={loading}
      onPress={onPress}
      className={`${bg} h-12 rounded-xl flex-row items-center justify-center px-4 ${className}`}
    >
      {loading ? (
        <ActivityIndicator color={variant === 'primary' ? '#fff' : '#000'} />
      ) : (
        <Text className={`${text} font-bold text-base`}>{title}</Text>
      )}
    </TouchableOpacity>
  );
};
