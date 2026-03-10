package com.xcz.gateway.service.impl;

import com.google.code.kaptcha.Producer;
import com.xcz.common.cache.extend.CacheConstants;
import com.xcz.common.cache.extend.RedissonClientEnum;
import com.xcz.common.cache.utils.RedissonUtil;
import com.xcz.common.core.exception.CaptchaException;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.core.utils.sign.Base64;
import com.xcz.common.core.utils.uuid.IdUtils;
import com.xcz.common.core.web.vo.params.AjaxResult;
import com.xcz.gateway.config.properties.CaptchaProperties;
import com.xcz.gateway.service.ValidateCodeService;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;

/**
 * 验证码实现处理
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;
    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;
    @Autowired
    private CaptchaProperties captchaProperties;


    private RedissonClient redisson = RedissonUtil.getRedissonClient(RedissonClientEnum.DATABASE_0);

    /**
     * 生成验证码
     */
    @Override
    public AjaxResult createCaptcha() throws IOException, CaptchaException {
        AjaxResult ajax = AjaxResult.success();
        boolean captchaEnabled = captchaProperties.getEnabled();
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled) {
            return ajax;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();

        String capStr = null, code = null;
        BufferedImage image = null;

        String captchaType = captchaProperties.getType();
        // 生成验证码
        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            //算式和答案
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisson.getBucket(CacheConstants.CAPTCHA_CODE_KEY + uuid)
                .set(code, Duration.ofMillis(captchaProperties.getExpireTime()));
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCaptcha(String code, String uuid) throws CaptchaException {
        if (StringUtils.isEmpty(code)) {
            throw new CaptchaException("验证码不能为空");
        }
        if (StringUtils.isEmpty(uuid)) {
            throw new CaptchaException("验证码已失效");
        }
        String captcha = redisson.getBucket(CacheConstants.CAPTCHA_CODE_KEY + uuid)
                .get().toString();

        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException("验证码错误");
        }else{
            redisson.getBucket(CacheConstants.CAPTCHA_CODE_KEY + uuid).delete();
        }
    }
}
