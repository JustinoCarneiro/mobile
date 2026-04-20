# 13. Componentes de UI Base (Design System)

## 1. Diretrizes Visuais
- **Cores:** Azul Ceará (`#004595` ou `bg-blue-800`), Branco e Cinza claro para inputs.
- **Estilização:** Utilização exclusiva de **NativeWind** (Tailwind CSS).
- **Feedback:** Todos os componentes de ação devem suportar estados de `loading` e `disabled`.

## 2. Componente: `Button`
- **Props:** `title`, `onPress`, `isLoading`, `variant` (primary/secondary).
- **Estilo:** Cantos arredondados, texto centralizado, feedback tátil.

## 3. Componente: `Input`
- **Props:** `label`, `error`, `icon`, e todas as props padrão do `TextInput`.
- **Estilo:** Borda suave, destaque ao focar, exibição de mensagem de erro em vermelho abaixo do campo.