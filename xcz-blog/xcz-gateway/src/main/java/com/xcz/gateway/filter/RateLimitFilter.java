package com.xcz.gateway.filter;

import com.xcz.common.cache.extend.RedissonClientEnum;
import com.xcz.common.cache.utils.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiterReactive;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 限流过滤器 - 防止DDoS攻击
 */
@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Value("${security.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;
    //每秒最大请求
    @Value("${security.rate-limit.requests-per-second:100}")
    private int requestsPerSecond;
    //突发请求容量上线
    @Value("${security.rate-limit.burst-capacity:200}")
    private int burstCapacity;

    // 存储每个IP的令牌桶
    private final RedissonClient redisson = RedissonUtil.getRedissonClient(RedissonClientEnum.DATABASE_0);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!rateLimitEnabled) {
            return chain.filter(exchange);
        }

        String clientIp = getClientIpAddress(exchange);

        // 获取 Reactive 限流器
        RRateLimiterReactive limiter = redisson.reactive().getRateLimiter(clientIp);

        // 链式调用：设置规则 -> 设置过期 -> 尝试获取 -> 处理结果
        return limiter.trySetRate(RateType.OVERALL, requestsPerSecond, Duration.ofSeconds(1))
                .flatMap(setResult -> limiter.expire(Duration.ofHours(1))) // 设置过期时间
                .then(limiter.tryAcquire()) // 尝试获取令牌
                .flatMap(allowed -> {
                    if (allowed) {
                        return chain.filter(exchange);
                    } else {
                        log.warn("请求被限流，IP: {}", clientIp);
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        return exchange.getResponse().setComplete();
                    }
                });
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }

    @Override
    public int getOrder() {
        // 设置过滤器顺序，通常在认证过滤器之前
        return -200;
    }
}
