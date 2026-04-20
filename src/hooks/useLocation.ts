import { useState, useEffect } from 'react';
import * as Location from 'expo-location';
import { Alert } from 'react-native';

interface LocationCoords {
  latitude: number;
  longitude: number;
}

export const useLocation = () => {
  const [coords, setCoords] = useState<LocationCoords | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const { status } = await Location.requestForegroundPermissionsAsync();
        
        if (status !== 'granted') {
          const msg = 'Permissão de localização negada. O Marketplace Ceará precisa do GPS para encontrar prestadores próximos.';
          setErrorMsg(msg);
          Alert.alert('Permissão Necessária', msg);
          setLoading(false);
          return;
        }

        const location = await Location.getCurrentPositionAsync({
          accuracy: Location.Accuracy.Balanced,
        });

        const newCoords = {
          latitude: location.coords.latitude,
          longitude: location.coords.longitude,
        };

        // Protocolo de Verificação QA (Linux/Emulador)
        console.log('[QA] Localização capturada com sucesso:', newCoords);
        
        setCoords(newCoords);
      } catch (error: any) {
        console.error('[Error] Falha ao obter localização:', error);
        setErrorMsg('Não foi possível obter sua localização atual.');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  return { coords, errorMsg, loading };
};
