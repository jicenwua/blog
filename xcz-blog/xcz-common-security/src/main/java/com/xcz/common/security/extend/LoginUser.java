package com.xcz.common.security.extend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户信息
 */
@Data
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户名
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 账户状态
     */
    private Boolean enabled;

    /**
     * 账户锁定状态
     */
    private boolean accountNonLocked;

    /**
     * 凭证过期状态
     */
    private boolean credentialsNonExpired;

    /**
     * 账户过期状态
     */
    private boolean accountNonExpired;

    public LoginUser() {
    }

    public LoginUser(Long userId,String username, String name,String password, Boolean enabled, Set<String> permissions) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.permissions = permissions;
    }

    public LoginUser(Long userId,String username,String name, String password, Boolean enabled, boolean accountNonLocked,
                     boolean credentialsNonExpired, boolean accountNonExpired, Set<String> permissions) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonExpired = accountNonExpired;
        this.permissions = permissions;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * 账户是否未锁定
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * 凭证是否未过期
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * 账户是否启用
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissions == null || permissions.isEmpty()) {
            return new HashSet<>();
        }
        // 将权限字符串转换为GrantedAuthority对象
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission))
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否有指定权限
     */
    public boolean hasPermission(String permission) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        return permissions.contains(permission);
    }
}
