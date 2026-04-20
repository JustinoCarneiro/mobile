# 6. Especificação: Abertura de Chamado Multimídia (US04)

## 1. História de Usuário
- [cite_start]**US04:** Como Cliente, eu quero solicitar um serviço enviando um texto, um áudio ou uma foto do meu problema, para que o prestador avalie a gravidade. 

## 2. Regras de Negócio
- [cite_start]**Upload:** O sistema deve aceitar arquivos de imagem (jpg/png) ou áudio. [cite: 128, 203]
- [cite_start]**Armazenamento:** Os arquivos devem ser enviados para o storage (MinIO local / S3 produção) e apenas a URL pública deve ser salva no banco de dados. [cite: 136, 256]
- [cite_start]**Status Inicial:** Todo novo chamado nasce com o status `PENDENTE`. 
- [cite_start]**Segurança:** Apenas usuários com a Role `CLIENT` podem abrir chamados. 

## 3. Especificação Técnica
- [cite_start]**Endpoint:** `POST /api/v1/services/requests` [cite: 135, 246]
- **Payload:** `multipart/form-data` contendo:
    - `description` (String)
    - `providerId` (UUID)
    - `file` (MultipartFile - opcional)
- [cite_start]**Entidade `ServiceRequest`:** ID, Cliente_ID, Prestador_ID, Descrição, URL_Midia, Status.