# 12. Especificação de UI: Login e Cadastro (US01 e US02)

## 1. Fluxo de Navegação
- **Auth Stack:** Login -> Cadastro (Cliente ou Prestador).
- **Redirecionamento:** Após login bem-sucedido, o usuário deve ser levado para a tela de 'Home' (que criaremos no próximo passo).

## 2. Requisitos de Tela (Login)
- Campos: E-mail e Senha.
- Validação: E-mail deve ser válido; senha não pode estar vazia.
- Ação: Chamar `POST /api/v1/auth/login` e salvar a resposta na `authStore`.

## 3. Requisitos de Tela (Cadastro)
- Seleção de Perfil: Botões para escolher entre "Sou Cliente" ou "Quero Trabalhar".
- Campos Comuns: Nome Completo, E-mail, CPF, Senha.
- Campos de Prestador (US02): Categoria (ex: Encanador, Eletricista) e Bio Curta.

## 4. Design System (NativeWind/Tailwind)
- Cores: Azul institucional (Ceará) e Branco.
- Feedback: Exibir um `ActivityIndicator` (spinner) durante as chamadas de API.