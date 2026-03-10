package com.xcz.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;


@Component
@RefreshScope
@Slf4j
public class CacheRequestFilter extends AbstractGatewayFilterFactory<CacheRequestFilter.Config> {
    public CacheRequestFilter() {
        super(Config.class);
    }

    @Override
    public String name() {
        return "CacheRequestFilter";
    }

    @Override
    public GatewayFilter apply(Config config) {
        // 创建实际执行逻辑的内部类对象
        CacheRequestGatewayFilter cacheRequestGatewayFilter = new CacheRequestGatewayFilter();
        Integer order = config.getOrder();
        // 如果没有配置优先级(order)，直接返回过滤器
        if (order == null) {
            return cacheRequestGatewayFilter;
        }
        // 如果配置了 order，则包装成一个带顺序的过滤器，值越小优先级越高
        return new OrderedGatewayFilter(cacheRequestGatewayFilter, order);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        // 支持在 yml 中简写配置，例如：CacheRequestFilter=100 (100会映射到order字段)
        return Collections.singletonList("order");
    }

    /**
     * 内部类：真正实现"缓存请求体"逻辑的地方
     */
    public static class CacheRequestGatewayFilter implements GatewayFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            String path = exchange.getRequest().getURI().getPath();

            //检查请求方法，GET 和 DELETE 通常没有请求体，不需要缓存，直接放行
            HttpMethod method = exchange.getRequest().getMethod();
            if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
                return chain.filter(exchange);
            }

            // ⭐ 添加：跳过 multipart/form-data 请求（文件上传）
            String contentType = exchange.getRequest().getHeaders().getFirst("Content-Type");
            if (contentType != null && contentType.toLowerCase().contains("multipart/form-data")) {
                // 文件上传请求不缓存，直接放行
                return chain.filter(exchange);
            }

            // 利用网关内置工具类 cacheRequestBodyAndRequest 缓存数据
            // 这个方法会读取原来的 Body 流，存入内存，并生成一个新的可多次读取的 Request 对象
            log.info("💾 CacheRequestFilter 处理：{}", path);
            return ServerWebExchangeUtils.cacheRequestBodyAndRequest(exchange, (serverHttpRequest) -> {
                // 如果生成的请求对象没变，说明不需要特殊处理，继续执行后续过滤器
                if (serverHttpRequest == exchange.getRequest()) {
                    log.info("💾 请求未变化，直接放行：{}", path);
                    return chain.filter(exchange);
                }
                // 使用 mutate() 重新构建 exchange，将包含"已缓存 Body"的新请求对象传递下去
                log.info("💾 已缓存请求体：{}", path);
                return chain.filter(exchange.mutate().request(serverHttpRequest).build());
            });
        }
    }

    @Data
    static class Config {
        private Integer order = Ordered.HIGHEST_PRECEDENCE;
    }
}
