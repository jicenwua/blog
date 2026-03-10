package com.xcz.common.core.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限相关通用常量
 */
public class SecurityConstants {
    /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * 授权信息字段
     */
    public static final String AUTHORIZATION_HEADER = "authorization";

    /**
     * 请求来源
     */
    public static final String FROM_SOURCE = "from-source";

    /**
     * 内部请求
     */
    public static final String INNER = "inner";

    /**
     * 用户标识
     */
    public static final String USER_KEY = "user_key";

    /**
     * 登录用户
     */
    public static final String LOGIN_USER = "login_user";

    /**
     * 角色权限
     */
    public static final String ROLE_PERMISSION = "role_permission";

    /**
     * 内部请求白名单
     */
    public static final Set<String> INNER_WHITELIST = new HashSet<>();

    static {
        // 用户认证接口
        INNER_WHITELIST.add("/user/login");
        INNER_WHITELIST.add("/user/register");
        
        // 测试接口
        INNER_WHITELIST.add("/test/**");
        
        // 认证相关接口
        INNER_WHITELIST.add("/auth/logout");
        INNER_WHITELIST.add("/auth/refresh");
        
        // 静态资源和系统接口
        INNER_WHITELIST.add("/favicon.ico");
        INNER_WHITELIST.add("/actuator/**");
        INNER_WHITELIST.add("/v3/api-docs/**");
        INNER_WHITELIST.add("/webjars/**");
        INNER_WHITELIST.add("/swagger-ui/**");
        INNER_WHITELIST.add("/doc.html");
        INNER_WHITELIST.add("/static/**");
    }
}
