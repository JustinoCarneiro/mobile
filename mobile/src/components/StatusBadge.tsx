import React from 'react';
import { View, Text } from 'react-native';
import { ServiceRequestStatus } from '../types';

interface StatusBadgeProps {
  status: ServiceRequestStatus;
}

export const StatusBadge = ({ status }: StatusBadgeProps) => {
  let bgColor = 'bg-gray-100';
  let textColor = 'text-gray-600';
  let label = status.toString();

  switch (status) {
    case ServiceRequestStatus.PENDING:
      bgColor = 'bg-yellow-100';
      textColor = 'text-yellow-700';
      label = 'Pendente';
      break;
    case ServiceRequestStatus.ACCEPTED:
    case ServiceRequestStatus.PAYMENT_CONFIRMED:
      bgColor = 'bg-blue-100';
      textColor = 'text-blue-700';
      label = status === ServiceRequestStatus.ACCEPTED ? 'Aceito' : 'Garantido (Escrow)';
      break;
    case ServiceRequestStatus.COMPLETED:
      bgColor = 'bg-green-100';
      textColor = 'text-green-700';
      label = 'Concluído';
      break;
    case ServiceRequestStatus.REJECTED:
    case ServiceRequestStatus.CANCELED:
      bgColor = 'bg-red-100';
      textColor = 'text-red-700';
      label = status === ServiceRequestStatus.REJECTED ? 'Recusado' : 'Cancelado';
      break;
  }

  return (
    <View className={`${bgColor} px-3 py-1 rounded-full items-center justify-center`}>
      <Text className={`${textColor} text-xs font-bold`}>{label}</Text>
    </View>
  );
};
