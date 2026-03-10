package com.xcz.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 请求加密设置
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties("security")
public class EncryptionProperties {
    /**
     * 是否开启加密签名
     */
    private boolean signature = false;
    /**
     * 是否开启加密
     */
    private boolean enable = false;
    /**
     * 加密密钥（至少32位）
     */
    private String  secretKey = "defaultSecretKey123456";
}
