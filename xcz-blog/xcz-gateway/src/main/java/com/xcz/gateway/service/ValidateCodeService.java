package com.xcz.gateway.service;


import com.xcz.common.core.exception.CaptchaException;
import com.xcz.common.core.web.vo.params.AjaxResult;

import java.io.IOException;

/**
 * 验证码处理
 */
public interface ValidateCodeService {
    /**
     * 生成验证码
     */
    AjaxResult createCaptcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    void checkCaptcha(String key, String value) throws CaptchaException;
}
