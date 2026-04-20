package com.marketplace.ceara.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Configuração do cliente S3.
 * <p>
 * Aponta para o MinIO local por padrão.
 * Para usar o AWS S3 real em produção, basta definir as variáveis de ambiente:
 * <ul>
 *   <li>STORAGE_S3_ENDPOINT (remover ou usar o endpoint da região AWS)</li>
 *   <li>STORAGE_S3_ACCESS_KEY</li>
 *   <li>STORAGE_S3_SECRET_KEY</li>
 *   <li>STORAGE_S3_REGION</li>
 * </ul>
 */
@Configuration
public class S3Config {

    @Value("${storage.s3.endpoint}")
    private String endpoint;

    @Value("${storage.s3.access-key}")
    private String accessKey;

    @Value("${storage.s3.secret-key}")
    private String secretKey;

    @Value("${storage.s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .forcePathStyle(true) // Necessário para MinIO
                .build();
    }
}
