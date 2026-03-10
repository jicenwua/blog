package com.xcz.common.security.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

/**
 * JWT 配置属性
 */
@Slf4j
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    /**
     * JWT 密钥
     */
    private String secret;

    /**
     * 过期时间（毫秒）
     */
    private Long expiration;

    /**
     * 获取 JWT 密钥，如果未配置则使用默认值
     */
    public String getSecret() {
        if (secret == null || secret.isEmpty()) {
            log.warn("JWT Secret 未配置，使用默认值！");
            return "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz123456";
        }
        return secret;
    }

    /**
     * 获取过期时间，如果未配置则使用默认值（7 天）
     */
    public Long getExpiration() {
        if (expiration == null) {
            log.warn("JWT Expiration 未配置，使用默认值 7 天！");
            return 604800000L; // 7 天
        }
        return expiration;
    }
}
