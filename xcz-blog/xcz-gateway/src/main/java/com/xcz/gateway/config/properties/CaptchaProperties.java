package com.xcz.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "security.captcha")
public class CaptchaProperties {
    /**
     * 验证码开关
     */
    private Boolean enabled = true;

    /**
     * 验证码类型（math 数组计算 char 字符）
     */
    private String type = "char";

    /**
     * 验证码有效期，默认为3分钟
     */
    private Long expireTime = 3 * 60 * 1000L;
}
