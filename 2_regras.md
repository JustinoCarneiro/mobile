# 2. Regras de Negócio e Blindagem

## Fluxo de Cadastro
- [cite_start]Prestadores devem passar por validação de CPF (Background Check) antes da ativação do perfil[cite: 2, 7, 8].

## Localização e Busca
- [cite_start]A busca geolocalizada deve calcular o raio em KM diretamente via banco de dados[cite: 1, 7, 8].

## Gestão Financeira (Escrow)
- [cite_start]O valor pago pelo cliente fica retido na plataforma até que o serviço seja marcado como concluído[cite: 3, 7].
- [cite_start]Integração com Webhooks para confirmação automática de pagamentos[cite: 3, 8].

## Blindagem Técnica
- [cite_start]**Padrão de Sintaxe:** A arquitetura baseada em camadas (Controller, Service, Repository) não deve ser alterada[cite: 8].
- [cite_start]**Ambiente:** Todo o desenvolvimento e scripts de automação devem ser otimizados para Linux[cite: 6, 8].
- **⚠️ NativeWind — Proibido `shadow-*` via className:** No mobile, **NUNCA** usar classes utilitárias de sombra do NativeWind (`shadow-sm`, `shadow-md`, `shadow-lg`, etc.) via `className`. O `cssInterop` do NativeWind v4 cria wrappers que quebram a propagação do contexto do `NavigationContainer` no Android, causando o erro fatal `MISSING_CONTEXT_ERROR`. **Usar sempre estilos inline nativos:** `style={{ elevation: 2, shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.05, shadowRadius: 2 }}`.