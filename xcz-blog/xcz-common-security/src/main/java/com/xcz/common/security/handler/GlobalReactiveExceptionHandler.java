package com.xcz.common.security.handler;

import com.xcz.common.core.constant.HttpStatus;
import com.xcz.common.core.exception.DemoModeException;
import com.xcz.common.core.exception.InnerAuthException;
import com.xcz.common.core.exception.ServiceException;
import com.xcz.common.core.exception.auth.NotPermissionException;
import com.xcz.common.core.exception.auth.NotRoleException;
import com.xcz.common.core.exception.base.BaseException;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.core.web.vo.params.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

/**
 * 全局异常处理器 (Reactive 版本 - 给 Gateway 用)
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GlobalReactiveExceptionHandler {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public AjaxResult handleNotPermissionException(NotPermissionException e, ServerHttpRequest request) {
        // 变化点：获取路径的方式
        String requestURI = request.getPath().value();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public AjaxResult handleNotRoleException(NotRoleException e, ServerHttpRequest request) {
        String requestURI = request.getPath().value();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持 (WebFlux版)
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public AjaxResult handleMethodNotAllowedException(MethodNotAllowedException e, ServerHttpRequest request) {
        String requestURI = request.getPath().value();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getSupportedMethods());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, ServerHttpRequest request) {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, ServerHttpRequest request) {
        String requestURI = request.getPath().value();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, ServerHttpRequest request) {
        String requestURI = request.getPath().value();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * WebFlux 参数绑定异常 (对应 Servlet 的 BindException)
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public AjaxResult handleWebExchangeBindException(WebExchangeBindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 兼容 Servlet 风格的验证异常 (虽然 WebFlux 较少抛出这个，但保留以防万一)
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 请求参数格式错误 (比如 JSON 格式不对)
     */
    @ExceptionHandler(ServerWebInputException.class)
    public AjaxResult handleServerWebInputException(ServerWebInputException e) {
        log.error("参数解析失败", e);
        return AjaxResult.error("请求参数格式错误: " + e.getReason());
    }

    /**
     * 内部认证异常
     */
    @ExceptionHandler(InnerAuthException.class)
    public AjaxResult handleInnerAuthException(InnerAuthException e) {
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException(DemoModeException e) {
        return AjaxResult.error("演示模式，不允许操作");
    }
}
