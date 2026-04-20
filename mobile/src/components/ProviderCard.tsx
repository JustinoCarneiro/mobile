import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { NearbyProviderDTO } from '../types';

interface ProviderCardProps {
  provider: NearbyProviderDTO;
  onPress: () => void;
}

export const ProviderCard = ({ provider, onPress }: ProviderCardProps) => {
  return (
    <TouchableOpacity
      onPress={onPress}
      activeOpacity={0.8}
      className="bg-white p-4 rounded-2xl mb-4 border border-gray-100 flex-row items-center"
      style={{
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.05,
        shadowRadius: 10,
        elevation: 2,
      }}
    >
      {/* Avatar Placeholder */}
      <View className="w-14 h-14 bg-blue-100 rounded-full items-center justify-center mr-4">
        <Text className="text-blue-800 font-bold text-xl">
          {provider.fullName.charAt(0).toUpperCase()}
        </Text>
      </View>

      <View className="flex-1">
        <View className="flex-row justify-between items-start">
          <Text className="text-lg font-bold text-gray-800 flex-1 mr-2" numberOfLines={1}>
            {provider.fullName}
          </Text>
          <View className="bg-yellow-100 px-2 py-0.5 rounded-md flex-row items-center">
            <Text className="text-yellow-700 text-xs font-bold mr-1">★</Text>
            <Text className="text-yellow-700 text-xs font-bold">
              {provider.ratingAverage.toFixed(1)}
            </Text>
          </View>
        </View>

        <Text className="text-gray-500 mb-1">{provider.category}</Text>

        <View className="flex-row items-center">
          <Text className="text-xs text-gray-400 mr-2">📍 {provider.distanceKm.toFixed(1)} km de distância</Text>
        </View>
      </View>

      <Text className="text-blue-800 font-bold ml-2">➔</Text>
    </TouchableOpacity>
  );
};
