package com.xcz.gateway.filter;

import com.xcz.common.core.constant.SecurityHeaderConstants;
import com.xcz.common.core.utils.CryptoUtils;
import com.xcz.gateway.config.properties.EncryptionProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 响应加密过滤器 - 对网关返回的响应数据进行加密
 */
@Slf4j
@Component
public class ResponseEncryptionFilter implements GlobalFilter, Ordered {

    @Resource
    private EncryptionProperties encryptionProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!encryptionProperties.isEnable()) {
            return chain.filter(exchange);
        }

        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body == null) {
                    return super.writeWith(body);
                }

                return DataBufferUtils.join(body)
                        .flatMap(dataBuffer -> {
                            try {
                                // 读取原始响应数据
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);

                                // 生成IV并加密响应数据
                                String iv = CryptoUtils.generateIv();
                                String originalResponseBody = new String(content, StandardCharsets.UTF_8);
                                String encryptedResponse = CryptoUtils.aesEncrypt(
                                    originalResponseBody,
                                    encryptionProperties.getSecretKey(),
                                    iv
                                );

                                // 创建加密后的响应体
                                byte[] encryptedBytes = encryptedResponse.getBytes(StandardCharsets.UTF_8);
                                DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                                DataBuffer encryptedDataBuffer = bufferFactory.wrap(encryptedBytes);

                                // 添加加密标识头和IV头
                                super.getHeaders().add(SecurityHeaderConstants.ENCRYPTED_RESPONSE, "true");
                                super.getHeaders().add(SecurityHeaderConstants.INITIALIZATION_VECTOR, iv);

                                return super.writeWith(Mono.just(encryptedDataBuffer));
                            } catch (Exception e) {
                                log.error("响应数据加密失败", e);
                                return super.writeWith(body);
                            }
                        });
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        // 设置过滤器顺序，通常是最后执行
        return Integer.MAX_VALUE;
    }
}
