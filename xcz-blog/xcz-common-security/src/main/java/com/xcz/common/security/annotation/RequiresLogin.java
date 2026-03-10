package com.xcz.common.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * 登录认证：只有登录之后才能进入该方法 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@Documented
@PreAuthorize("isAuthenticated()")
public @interface RequiresLogin {
}
