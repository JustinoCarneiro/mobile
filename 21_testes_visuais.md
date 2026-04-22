# 21. Estratégia de Testes Visuais e Componentes

## 1. Storybook (Visual de Componentes)
- **Objetivo:** Garantir que o `Button` e o `Input` não "quebrem" visualmente ao longo do desenvolvimento.
- **Casos de Uso:** - Ver o botão com o `ActivityIndicator` (loading).
    - Ver o input com a borda vermelha (erro).

## 2. Maestro (Visual de Fluxo/E2E)
- **Objetivo:** Validar o "Caminho Feliz" do usuário visualmente.
- **Fluxo Principal:** 1. Abrir o App.
    2. Digitar e-mail/senha.
    3. Clicar em Entrar.
    4. Validar se a Home carregou a lista de profissionais.