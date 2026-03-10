package com.xcz.common.security.config;

import com.xcz.common.security.interceptor.HeaderAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    @Autowired
    private HeaderAuthenticationFilter headerAuthenticationFilter;

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private IgnoreWhiteProperties ignoreWhiteProperties;

    /**
     * 密码编码器
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     *
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤器链
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        List<String> whites = ignoreWhiteProperties.getWhites();
        http
                // 基于token，不需要csrf
                .csrf(csrf -> csrf.disable())
                // 不创建会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 过滤请求，为登录用户可以通过，但是是事先进行headerauthen判断，是否登录，再验证这个白名单
                .authorizeHttpRequests(authz -> authz
                                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(whites.toArray(new String[0])).permitAll()
//                        // 允许访问静态资源
//                        .requestMatchers("/static/**").permitAll()
//                        // 允许访问swagger相关接口
//                        .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
//                        // 允许访问登录接口
//                        .requestMatchers("/login", "/auth/login").permitAll()
//                        // 允许访问注册接口
//                        .requestMatchers("/register", "/auth/register").permitAll()
//                        // 允许访问健康检查接口
//                        .requestMatchers("/health", "/actuator/**").permitAll()
//                        .requestMatchers("/test/**").permitAll()
                        // 所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 添加jwt过滤器
                .addFilterBefore(headerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
