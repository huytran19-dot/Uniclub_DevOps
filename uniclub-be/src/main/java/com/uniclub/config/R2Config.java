package com.uniclub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class R2Config {

    private static final Logger log = LoggerFactory.getLogger(R2Config.class);

    @Value("${r2.account-id:}")
    private String accountId;

    @Value("${r2.access-key-id:}")
    private String accessKeyId;

    @Value("${r2.secret-key:}")
    private String secretKey;

    @Value("${r2.bucket-name:uniclub-images}")
    private String bucketName;

    @Value("${r2.public-url:https://pub-c85916fa887245a784c22c1c6dd353e8.r2.dev}")
    private String publicUrl;

    @Bean
    public S3Client s3Client() {
        if (accountId == null || accountId.isBlank() ||
            accessKeyId == null || accessKeyId.isBlank() ||
            secretKey == null || secretKey.isBlank()) {
            log.warn("Cloudflare R2 credentials or account-id are not fully configured. Using fallback configuration.");
        }

        String endpoint = String.format("https://%s.r2.cloudflarestorage.com",
                (accountId != null && !accountId.isBlank()) ? accountId.trim() : "dummy-account");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                (accessKeyId != null && !accessKeyId.isBlank()) ? accessKeyId.trim() : "dummy-key",
                (secretKey != null && !secretKey.isBlank()) ? secretKey.trim() : "dummy-secret"
        );

        log.info("Initializing Cloudflare R2 S3Client for bucket '{}' at endpoint '{}'", bucketName, endpoint);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto"))
                .build();
    }
}

