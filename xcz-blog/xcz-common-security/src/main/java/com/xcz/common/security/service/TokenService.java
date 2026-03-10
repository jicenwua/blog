package com.xcz.common.security.service;

import com.xcz.common.cache.extend.RedissonClientEnum;
import com.xcz.common.cache.utils.RedissonUtil;
import com.xcz.common.core.constant.TokenConstants;
import com.xcz.common.core.utils.JwtUtils;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.security.config.JwtProperties;
import com.xcz.common.security.extend.LoginUser;
import io.jsonwebtoken.Claims;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Function;

/**
 * Token服务类
 * 负责JWT Token的生成、解析和验证
 */
@Component
public class TokenService {

    @Autowired
    private JwtProperties jwtProperties;

    private static final String LOGIN_TOKEN_KEY = "login_tokens:";

    private RedissonClient redissonClient = RedissonUtil.getRedissonClient(RedissonClientEnum.DATABASE_0);


    private Function<String, RBucket<LoginUser>> getLoginUser = (token) -> {
        String userKey = LOGIN_TOKEN_KEY + getTokenKey(token);
        return redissonClient.getBucket(userKey);
    };

    /**
     * 创建令牌
     *
     * @param loginUser 登录用户
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        // 在创建token之前设置JWT密钥
        JwtUtils.setSecret(jwtProperties.getSecret());
        JwtUtils.setExpiration(jwtProperties.getExpiration());
        String token = JwtUtils.createToken(loginUser.getUsername(),loginUser.getUserId(), loginUser.getPermissions());

        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + jwtProperties.getExpiration());
        loginUser.setToken(token);
        getLoginUser.apply(token)
                .set(loginUser, Duration.ofMillis(jwtProperties.getExpiration()));

        return token;
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        // 确保JWT密钥已设置
        JwtUtils.setSecret(jwtProperties.getSecret());
        JwtUtils.setExpiration(jwtProperties.getExpiration());
        Claims claims = JwtUtils.parseJWT(token);
        return claims != null ? (String) claims.get(TokenConstants.DETAILS_USERNAME) : null;
    }

    /**
     * 验证令牌
     *
     * @param token    令牌
     * @param username 用户名
     * @return 是否有效
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return username.equals(tokenUsername) && !isTokenExpired(token);
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        // 确保JWT密钥已设置
        JwtUtils.setSecret(jwtProperties.getSecret());
        JwtUtils.setExpiration(jwtProperties.getExpiration());
        Claims claims = JwtUtils.parseJWT(token);
        if (claims == null) {
            return true;
        }
        long expiration = claims.getExpiration().getTime();
        return System.currentTimeMillis() >= expiration;
    }

    /**
     * 从令牌中获取Claims
     *
     * @param token 令牌
     * @return Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            // 确保JWT密钥已设置
            JwtUtils.setSecret(jwtProperties.getSecret());
            JwtUtils.setExpiration(jwtProperties.getExpiration());
            return JwtUtils.parseJWT(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录用户
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + jwtProperties.getExpiration());

        // 将用户信息存储到Redis中
        getLoginUser.apply(loginUser.getToken())
                .set(loginUser, Duration.ofMillis(jwtProperties.getExpiration()));
    }

    /**
     * 获取登录用户
     *
     * @param token 令牌
     * @return 登录用户
     */
    public LoginUser getLoginUser(String token) {
        return getLoginUser.apply(token).get();
    }

    /**
     * 删除用户缓存
     *
     * @param token 令牌
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            getLoginUser.apply(token).delete();
        }
    }

    /**
     * 验证令牌有效性
     *
     * @param loginUser 用户信息
     * @return 结果
     */
    public void verifyToken(LoginUser loginUser) {
        if (loginUser != null && StringUtils.isNotEmpty(loginUser.getToken())) {
            JwtUtils.parseJWT(loginUser.getToken());
        }
    }

    /**
     * 从令牌中获取用户标识
     *
     * @param token 令牌
     * @return 用户标识
     */
    private String getTokenKey(String token) {
        if (StringUtils.isEmpty(token)) {
            return "";
        }
        return token.replace(TokenConstants.PREFIX, "");
    }
}
