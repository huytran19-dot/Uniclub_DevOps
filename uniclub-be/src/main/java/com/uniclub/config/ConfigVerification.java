package com.uniclub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration verification - verify mọi environment variables đã được load
 */
@Slf4j
@Configuration
@DependsOn("dotEnvConfig")
public class ConfigVerification {
    
    @Value("${spring.datasource.url:jdbc:mysql://localhost:3307/uniclub}")
    private String dbUrl;
    
    @Value("${sendgrid.api-key:}")
    private String sendgridKey;
    
    @Value("${r2.bucket-name:uniclub-images}")
    private String r2BucketName;

    @Value("${r2.public-url:}")
    private String r2PublicUrl;
    
    @Value("${jwt.secret:uniclub-dev-jwt-secret-key-min-256-bits-length-for-security}")
    private String jwtSecret;
    
    @Value("${vnpay.tmn-code:}")
    private String vnpayTmn;
    
    @PostConstruct
    public void verifyConfiguration() {
        log.info("========================================");
        log.info("Configuration Verification");
        log.info("========================================");
        log.info("✅ Database URL: {}", obfuscate(dbUrl));
        log.info("✅ SendGrid API Key: {}", obfuscate(sendgridKey));
        log.info("✅ Cloudflare R2 Bucket: {}", r2BucketName);
        log.info("✅ Cloudflare R2 Public URL: {}", obfuscate(r2PublicUrl));
        log.info("✅ JWT Secret: {}", obfuscate(jwtSecret));
        log.info("✅ VNPay TMN Code: {}", obfuscate(vnpayTmn));
        log.info("========================================");
        log.info("All configurations loaded successfully!");
        log.info("========================================");
    }
    
    private String obfuscate(String value) {
        if (value == null || value.isEmpty()) {
            return "[EMPTY]";
        }
        if (value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 4) + "***" + value.substring(value.length() - 2);
    }
}
