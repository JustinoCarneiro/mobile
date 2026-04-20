import React from 'react';
import { TouchableOpacity, Text } from 'react-native';

interface CategoryItemProps {
  label: string;
  icon: string;
  isSelected: boolean;
  onPress: () => void;
}

export const CategoryItem = ({ label, icon, isSelected, onPress }: CategoryItemProps) => {
  return (
    <TouchableOpacity
      onPress={onPress}
      activeOpacity={0.7}
      className={`mr-4 px-4 py-2 rounded-full border flex-row items-center ${
        isSelected 
          ? 'bg-blue-800 border-blue-800' 
          : 'bg-white border-gray-200'
      }`}
    >
      <Text className="mr-2">{icon}</Text>
      <Text 
        className={`font-semibold ${
          isSelected ? 'text-white' : 'text-gray-600'
        }`}
      >
        {label}
      </Text>
    </TouchableOpacity>
  );
};
