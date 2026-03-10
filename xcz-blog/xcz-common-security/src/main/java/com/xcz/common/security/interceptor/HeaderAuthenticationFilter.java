package com.xcz.common.security.interceptor;

import com.xcz.common.core.constant.TokenConstants;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.security.config.IgnoreWhiteProperties;
import com.xcz.common.security.extend.LoginUser;
import com.xcz.common.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Header认证过滤器
 * 作为Spring Security过滤器负责设置Security上下文
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private IgnoreWhiteProperties ignoreWhiteProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getToken(request);
        log.info("收到请求：{}",request.getRequestURI());

        //验证token是否有效，有效则重新添加到啊权限上下文
        if (StringUtils.isNotEmpty(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            LoginUser loginUser = tokenService.getLoginUser(token);
            if (loginUser != null && tokenService.validateToken(token, loginUser.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 获取请求中的token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(TokenConstants.AUTHENTICATION);
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, "");
        }
        return token;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        // 如果是白名单路径则跳过过滤
        boolean shouldSkip = ignoreWhiteProperties.getWhites().stream()
                .anyMatch(pattern -> org.springframework.util.PatternMatchUtils.simpleMatch(pattern, uri));

        return shouldSkip;
    }
}
