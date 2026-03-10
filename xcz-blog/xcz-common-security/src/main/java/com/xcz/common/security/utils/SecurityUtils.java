package com.xcz.common.security.utils;

import com.xcz.common.core.constant.TokenConstants;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.security.extend.LoginUser;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;

import java.util.Collection;

/**
 * 安全服务工具类
 */
public class SecurityUtils {

    /**
     * 获取用户账户名称
     */
    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户
     */
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息异常", e);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 该方法用于判断当前用户是否具有指定权限
     * @param permission 权限
     * @return 结果
     */
    public static boolean hasPermi(String permission) {
        return hasPermiOrRole("permission", permission);
    }

    /**
     * 该方法用于判断当前用户是否具有指定角色
     * @param role 角色
     * @return 结果
     */
    public static boolean hasRole(String role) {
        return hasPermiOrRole("role", role);
    }

    /**
     * 该方法用于判断当前用户是否具有指定权限或角色
     * @param type 类型
     * @param value 值
     * @return 结果
     */
    private static boolean hasPermiOrRole(String type, String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getPermissions() == null) {
            return false;
        }
        Collection<String> authorities = loginUser.getPermissions();
        return authorities.stream().anyMatch(x -> PatternMatchUtils.simpleMatch(value, x));
    }

    /**
     * 获取请求token
     */
    public static String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TokenConstants.AUTHENTICATION);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, StringUtils.EMPTY);
        }
        return token;
    }
}
