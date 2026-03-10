package com.xcz.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关日志过滤器 - 用于调试路由转发
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().toString();
        String contentType = exchange.getRequest().getHeaders().getFirst("Content-Type");

        log.info("========================================");
        log.info("🔵 网关收到请求");
        log.info("请求方法: {}", method);
        log.info("请求路径: {}", path);
        log.info("Content-Type: {}", contentType);
        log.info("完整URI: {}", exchange.getRequest().getURI());

        return chain.filter(exchange)
                .doOnError(error -> {
                    log.error("❌ 网关处理请求时发生错误", error);
                    log.error("错误类型: {}", error.getClass().getName());
                    log.error("错误信息: {}", error.getMessage());
                })
                .then(Mono.fromRunnable(() -> {
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : -1;
                    log.info("🟢 网关响应状态：{}", exchange.getResponse().getStatusCode());
                    log.info("响应状态码数值：{}", statusCode);
                    log.info("========================================");
                }));
    }

    @Override
    public int getOrder() {
        // 设置为最高优先级，最先执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
