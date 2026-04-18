import "./global.css";
import 'react-native-gesture-handler';
import React from 'react';
import { registerRootComponent } from 'expo';
import { NavigationContainer } from '@react-navigation/native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import App from './App';

/**
 * Root Component - Camada Zero.
 * 
 * Implementado em JS Puro sem tags JSX para garantir que o Babel não
 * aplique nenhuma HOC ou transformação que possa interceptar o Contexto.
 */
function Root() {
  return React.createElement(
    SafeAreaProvider,
    null,
    React.createElement(
      NavigationContainer,
      null,
      React.createElement(App, null)
    )
  );
}

registerRootComponent(Root);
