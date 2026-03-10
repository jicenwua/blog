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
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<AjaxResult> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AjaxResult.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权"));
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ResponseEntity<AjaxResult> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AjaxResult.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权"));
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<AjaxResult> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                          HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return ResponseEntity.badRequest().body(AjaxResult.error(e.getMessage()));
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<AjaxResult> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ?
                ResponseEntity.status(code).body(AjaxResult.error(code, e.getMessage())) :
                ResponseEntity.badRequest().body(AjaxResult.error(e.getMessage()));
    }

    /**
     * 基础业务异常
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<AjaxResult> handleBaseException(BaseException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生业务异常.'{}'", requestURI, e.getDefaultMessage());
        int code = e.getCode();
        String message = e.getDefaultMessage() != null ? e.getDefaultMessage() : e.getMessage();
        return code != 0 ?
                ResponseEntity.badRequest().body(AjaxResult.error(code, message)) :
                ResponseEntity.badRequest().body(AjaxResult.error(message));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AjaxResult> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return ResponseEntity.internalServerError().body(AjaxResult.error(e.getMessage()));
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AjaxResult> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return ResponseEntity.internalServerError().body(AjaxResult.error(e.getMessage()));
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<AjaxResult> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(AjaxResult.error(message));
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AjaxResult> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(AjaxResult.error(message));
    }

    /**
     * 内部认证异常
     */
    @ExceptionHandler(InnerAuthException.class)
    public ResponseEntity<AjaxResult> handleInnerAuthException(InnerAuthException e) {
        return ResponseEntity.badRequest().body(AjaxResult.error(e.getMessage()));
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public ResponseEntity<AjaxResult> handleDemoModeException(DemoModeException e) {
        return ResponseEntity.badRequest().body(AjaxResult.error("演示模式，不允许操作"));
    }

}
