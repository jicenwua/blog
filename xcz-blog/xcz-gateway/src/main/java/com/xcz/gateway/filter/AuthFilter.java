package com.xcz.gateway.filter;

import com.xcz.common.cache.extend.CacheConstants;
import com.xcz.common.cache.extend.RedissonClientEnum;
import com.xcz.common.cache.utils.RedissonUtil;
import com.xcz.common.core.constant.HttpStatus;
import com.xcz.common.core.constant.SecurityConstants;
import com.xcz.common.core.utils.JwtUtils;
import com.xcz.common.core.utils.ServletUtils;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.security.config.IgnoreWhiteProperties;
import com.xcz.common.security.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关鉴权
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    // 排除过滤的 uri 地址，nacos自行添加
    private final IgnoreWhiteProperties ignoreWhite;

    private final RedissonClient redisson = RedissonUtil.getRedissonClient(RedissonClientEnum.DATABASE_0);


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //获取请求路径
        String url = request.getURI().getPath();

        // 跳过不需要验证的路径
        if (StringUtils.matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        }

        //获取请求 token，验证 token
        String token = SecurityUtils.getToken(request);

        if (StringUtils.isEmpty(token)) {
            return unauthorizedResponse(exchange, "令牌不能为空");
        }
        Claims claims = JwtUtils.parseToken(token);
        if (claims == null) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }
        boolean islogin = redisson.getBucket(CacheConstants.LOGIN_TOKEN_KEY + token).isExists();
        if (!islogin) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }
        String userid = JwtUtils.getUserId(claims);
        String username = JwtUtils.getUserName(claims);
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(username)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        // 设置用户信息到请求
        ServerHttpRequest.Builder mutate = request.mutate();
        addHeader(mutate, SecurityConstants.USER_KEY, token);
        addHeader(mutate, SecurityConstants.DETAILS_USER_ID, userid);
        addHeader(mutate, SecurityConstants.DETAILS_USERNAME, username);
        // 内部请求来源参数清除
        removeHeader(mutate, SecurityConstants.FROM_SOURCE);
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    /**
     * 添加请求头
     * @param mutate    原请求头数据
     * @param name  请求头名
     * @param value 请求头值
     */
    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = ServletUtils.urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    /**
     * 去除请求头
     * @param mutate    原请求头信息
     * @param name  请求头名
     */
    private void removeHeader(ServerHttpRequest.Builder mutate, String name) {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }

    /**
     * 非法请求错误抛出
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
