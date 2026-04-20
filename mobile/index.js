import "./global.css";
import 'react-native-gesture-handler';
import { registerRootComponent } from 'expo';
import App from './App';

// O index.js volta a ser apenas o ponto de entrada simples.
// A lógica de navegação foi centralizada no App.tsx e AppNavigator.
registerRootComponent(App);
