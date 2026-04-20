package com.marketplace.ceara.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * Serviço de armazenamento de arquivos (MinIO/S3).
 * Utiliza o S3Client configurado em S3Config.
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final S3Client s3Client;

    @Value("${storage.s3.bucket}")
    private String bucketName;

    @Value("${storage.s3.endpoint}")
    private String endpoint;

    public FileStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Realiza o upload de um arquivo para o bucket S3.
     * Gera um nome único para o arquivo.
     *
     * @return URL final do arquivo.
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        
        try {
            log.info("Iniciando upload para S3: fileName={}, size={}", fileName, file.getSize());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Constrói a URL pública (ajustando para MinIO se necessário)
            String fileUrl = endpoint + "/" + bucketName + "/" + fileName;
            log.info("Upload concluído: url={}", fileUrl);
            
            return fileUrl;

        } catch (IOException e) {
            log.error("Erro ao ler arquivo para upload", e);
            throw new RuntimeException("Falha no processamento do arquivo de mídia", e);
        } catch (Exception e) {
            log.error("Erro ao realizar upload para S3", e);
            throw new RuntimeException("Falha na comunicação com o storage", e);
        }
    }
}
