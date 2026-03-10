package com.xcz.gateway.filter;

import com.xcz.common.core.constant.SecurityHeaderConstants;
import com.xcz.common.core.utils.CryptoUtils;
import com.xcz.common.core.utils.RequestSignatureUtils;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.security.config.IgnoreWhiteProperties;
import com.xcz.gateway.config.properties.EncryptionProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求加密过滤器 - 对进入网关的请求进行安全验证和加密处理
 */
@Slf4j
@Component
public class RequestEncryptionFilter implements GlobalFilter, Ordered {

    @Resource
    private EncryptionProperties encryptionProperties;
    @Resource
    private IgnoreWhiteProperties ignoreWhite;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //判断是否开启请求加密验证
        if (!encryptionProperties.isEnable()&& !encryptionProperties.isSignature()) {
            log.debug("🔐 请求加密/签名未开启，直接放行");
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getURI().getPath();
        log.info("🔐 RequestEncryptionFilter 处理：{}", requestUri);

        // 跳过静态资源和健康检查等路径
        if (shouldSkip(requestUri)) {
            log.info("🔐 白名单放行：{}", requestUri);
            return chain.filter(exchange);
        }

        // 验证请求签名
        if (encryptionProperties.isSignature() && !verifyRequestSignature(request)) {
            log.warn("请求签名验证失败: {}", requestUri);
            return handleUnauthorizedRequest(exchange);
        }

        // 解密请求数据
        if (encryptionProperties.isEnable()) {
            ServerWebExchange processedExchange = decryptRequestData(exchange);
            return chain.filter(processedExchange);
        }

        return chain.filter(exchange);
    }

    /**
     * 验证请求签名
     */
    private boolean verifyRequestSignature(ServerHttpRequest request) {
        String timestamp = request.getHeaders().getFirst(SecurityHeaderConstants.TIMESTAMP);
        String nonce = request.getHeaders().getFirst(SecurityHeaderConstants.NONCE);
        String signature = request.getHeaders().getFirst(SecurityHeaderConstants.SIGNATURE);

        if (!StringUtils.hasText(timestamp) || !StringUtils.hasText(nonce) || !StringUtils.hasText(signature)) {
            log.warn("缺少必要的安全头信息: timestamp={}, nonce={}, signature={}", timestamp, nonce, signature);
            return false;
        }

        // 验证时间戳（防重放攻击）
        long currentTime = System.currentTimeMillis();
        long requestTime = Long.parseLong(timestamp);
        if (Math.abs(currentTime - requestTime) > 5 * 60 * 1000) { // 5分钟内有效
            log.warn("请求时间戳已过期: {}", timestamp);
            return false;
        }

        // 构建参数Map（这里可以根据实际需要调整）
        Map<String, String> params = new HashMap<>();
        request.getQueryParams().forEach((key, values) -> {
            if (!values.isEmpty()) {
                params.put(key, values.get(0));
            }
        });

        // 验证签名
        return RequestSignatureUtils.verifySignature(params, timestamp, nonce, signature, encryptionProperties.getSecretKey());
    }

    /**
     * 解密请求数据
     */
    private ServerWebExchange decryptRequestData(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String encryptedData = request.getHeaders().getFirst(SecurityHeaderConstants.ENCRYPTED_BODY);

        if (StringUtils.hasText(encryptedData)) {
            try {
                // 从请求头获取IV进行解密
                String iv = getIvFromRequest(request);
                String decryptedBody = CryptoUtils.aesDecrypt(encryptedData, encryptionProperties.getSecretKey(), iv);

                // 创建装饰器来替换请求体
                ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(request) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        // 返回解密后的请求体
                        byte[] decodedBytes = decryptedBody.getBytes(StandardCharsets.UTF_8);
                        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                        DataBuffer bodyDataBuffer = bufferFactory.wrap(decodedBytes);
                        return Flux.just(bodyDataBuffer);
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        // 修改Content-Length和Content-Type
                        HttpHeaders headers = new HttpHeaders();
                        headers.addAll(super.getHeaders());
                        headers.remove(SecurityHeaderConstants.ENCRYPTED_BODY);
                        headers.setContentLength(decryptedBody.getBytes(StandardCharsets.UTF_8).length);
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        return headers;
                    }
                };

                return exchange.mutate().request(decoratedRequest).build();
            } catch (Exception e) {
                log.error("请求数据解密失败", e);
            }
        }

        return exchange;
    }

    /**
     * 从请求头中获取IV
     */
    private String getIvFromRequest(ServerHttpRequest request) {
        String iv = request.getHeaders().getFirst(SecurityHeaderConstants.INITIALIZATION_VECTOR);
        if (!StringUtils.hasText(iv)) {
            // 如果请求头中没有IV，则生成一个新的
            return CryptoUtils.generateIv();
        }
        return iv;
    }

    /**
     * 处理未授权请求
     */
    private Mono<Void> handleUnauthorizedRequest(ServerWebExchange exchange) {
        // 这里可以返回错误响应，具体实现取决于您的需求
        return exchange.getResponse().setComplete();
    }

    /**
     * 判断是否跳过安全检查
     */
    private boolean shouldSkip(String uri) {
        return StringUtils.matches(uri, ignoreWhite.getWhites());
    }

    @Override
    public int getOrder() {
        // 设置过滤器顺序，通常在认证过滤器之后
        return -100;
    }
}
