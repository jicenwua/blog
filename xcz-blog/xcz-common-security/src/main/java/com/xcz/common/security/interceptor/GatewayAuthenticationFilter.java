package com.xcz.common.security.interceptor;

import com.xcz.common.core.exception.base.BaseException;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.security.config.IgnoreWhiteProperties;
import com.xcz.common.security.extend.LoginUser;
import com.xcz.common.security.service.TokenService;
import com.xcz.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 获取token验证，判断用户是否登录过期，没有过期则重新构建用户权限上下文放行
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GatewayAuthenticationFilter implements WebFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getPath().value();

        if (org.springframework.http.HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        String token = SecurityUtils.getToken(request);

        if (StringUtils.isNotEmpty(token)) {
            // 注意：这里假设 tokenService.getLoginUser 是非阻塞的，或者很快
            // 如果涉及 Redis IO，在 Reactive 中最好也是异步的，但为了兼容旧代码，这里暂时同步调用
            LoginUser loginUser = tokenService.getLoginUser(token);

            if(loginUser == null){
                // Token 过期或无效，抛出认证异常，返回 401
                throw new BaseException(401, "登录已过期");
            }

            if (tokenService.validateToken(token, loginUser.getUsername())) {
                tokenService.verifyToken(loginUser);

                // 构建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

                // 【核心改变】在 WebFlux 中，必须使用 contextWrite 将认证信息写入 Reactor 上下文
                // 而不是 SecurityContextHolder.setContext
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            }
        }

        // 如果没有 Token 或校验失败，直接放行（交给 Security 配置决定是否拦截 401）
        return chain.filter(exchange);
    }

    private boolean pathMatches(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        } else if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return path.startsWith(prefix);
        } else {
            return path.equals(pattern);
        }
    }

}
