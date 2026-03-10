package com.xcz.common.security.config;


import com.xcz.common.security.interceptor.GatewayAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

/**
 * 只有在 Reactive 环境下（Gateway）才加载
 */
@Configuration
@EnableWebFluxSecurity  //用于启用webflux环境下的安全配置
@EnableReactiveMethodSecurity   //允许使用权限注解来验证权限
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)  //表示只有在Reactive Web应用环境下才会加载这个配置类
public class ReactiveSecurityConfig {

    //注入刚才新建的 Reactive 版过滤器
    @Autowired
    private GatewayAuthenticationFilter gatewayAuthenticationFilter;
    @Resource
    private IgnoreWhiteProperties ignoreWhiteProperties;



    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @RefreshScope
    public SecurityWebFilterChain reactiveFilterChain(ServerHttpSecurity http) {
        http
                // CSRF 禁用写法更新
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 无状态配置更新 (WebFlux 没有 SessionCreationPolicy)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                // 权限配置更新 (authorizeHttpRequests -> authorizeExchange)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS).permitAll()
                        .pathMatchers(ignoreWhiteProperties.getWhites().toArray(new String[0])).permitAll()
                        // 其他所有请求需要认证
                        .anyExchange().authenticated()
                )
                // 添加过滤器 (addFilterBefore -> addFilterAt, 且必须是 WebFilter)
                .addFilterAt(gatewayAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
