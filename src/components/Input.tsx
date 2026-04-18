import React from 'react';
import { View, Text, TextInput, TextInputProps } from 'react-native';

interface InputProps extends TextInputProps {
  label: string;
  error?: string;
  className?: string;
}

export const Input = ({ label, error, className = '', ...props }: InputProps) => {
  return (
    <View className={`mb-4 ${className}`}>
      <Text className="text-gray-700 font-semibold mb-1 ml-1">{label}</Text>
      <TextInput
        placeholderTextColor="#9ca3af"
        className={`bg-gray-100 h-12 px-4 rounded-xl border ${error ? 'border-red-500' : 'border-gray-200'} text-gray-900`}
        {...props}
      />
      {error && <Text className="text-red-500 text-xs mt-1 ml-1">{error}</Text>}
    </View>
  );
};
