package com.xcz.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 全局过滤器：传递客户端真实 IP
 */
@Component
public class GlobalIpFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取客户端原始 IP
        String clientIp = null;
        if (exchange.getRequest().getRemoteAddress() != null) {
            clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }
        }

        if (clientIp == null) {
            return chain.filter(exchange);
        }

        final String finalIp = clientIp;

        // 直接在原请求基础上 mutate，只修改或设置必要的 Header
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Real-IP", finalIp)
                        // 使用 header() 方法会覆盖已有的值，避免重复
                        .headers(headers -> {
                            // 处理 X-Forwarded-For 逻辑
                            List<String> forwardedFor = headers.get("X-Forwarded-For");
                            if (forwardedFor == null || forwardedFor.isEmpty()) {
                                headers.set("X-Forwarded-For", finalIp);
                            } else {
                                // 如果已存在，则追加（这通常是合法的）
                                headers.add("X-Forwarded-For", finalIp);
                            }
                        })
                        .build())
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        // 确保在 CORS 过滤器之前执行
        return -100;
    }
}
