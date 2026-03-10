package com.xcz.common.core.utils;

import com.xcz.common.core.constant.TokenConstants;
import com.xcz.common.core.text.Convert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jwt工具类
 */
public class JwtUtils {

    /**
     * 从外部传入密钥，而不是硬编码
     */
    private static String secret;

    /**
     * 从外部传入过期时间，而不是硬编码
     */
    private  static Long expiration ;

    /**
     * 设置密钥
     */
    public static void setSecret(String secret) {
        JwtUtils.secret = secret;
    }

    /**
     * 设置过期时间
     */
    public static void setExpiration(Long expiration) {
        JwtUtils.expiration = expiration;
    }

    /**
     * 创建JWT令牌
     *
     * @param subject 主题
     * @return 令牌
     */
    public static String createJWT(String subject) {
        SecureDigestAlgorithm<SecretKey, SecretKey> algorithm = Jwts.SIG.HS512;
        SecretKey key = getSecretKey();

        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + expiration; // 使用动态过期时间

        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(expMillis))
                .signWith(key, algorithm)
                .compact();
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims) {
        SecureDigestAlgorithm<SecretKey, SecretKey> algorithm = Jwts.SIG.HS512;
        SecretKey key = getSecretKey();

        return Jwts.builder()
                .claims(claims)
                .signWith(key, algorithm)
                .compact();
    }

    /**
     * 创建用户认证Token
     *
     * @param username 用户名
     * @param userId    用户id
     * @param permissions 权限列表
     * @return 令牌
     */
    public static String createToken(String username, long userId, Set<String> permissions) {
        SecureDigestAlgorithm<SecretKey, SecretKey> algorithm = Jwts.SIG.HS512;
        SecretKey key = getSecretKey();

        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + expiration; // 使用动态过期时间

        return Jwts.builder()
                .subject(username)
                .claim(TokenConstants.DETAILS_USERNAME, username)
                .claim(TokenConstants.DETAILS_USER_ID, userId)
                .claim(TokenConstants.CLAIMS_AUTHORITIES, permissions)
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(expMillis))
                .signWith(key, algorithm)
                .compact();
    }

    /**
     * 解析JWT令牌
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseJWT(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token) {
        return parseJWT(token);
    }

    /**
     * 根据令牌获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, TokenConstants.DETAILS_USER_ID);
    }

    /**
     * 根据身份信息获取用户ID
     *
     * @param claims 身份信息
     * @return 用户ID
     */
    public static String getUserId(Claims claims) {
        return getValue(claims, TokenConstants.DETAILS_USER_ID);
    }

    /**
     * 根据令牌获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUserName(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, TokenConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取用户名
     *
     * @param claims 身份信息
     * @return 用户名
     */
    public static String getUserName(Claims claims) {
        return getValue(claims, TokenConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取键值
     *
     * @param claims 身份信息
     * @param key    键
     * @return 值
     */
    public static String getValue(Claims claims, String key) {
        return Convert.toStr(claims.get(key), "");
    }

    /**
     * 获取加密用的 SecretKey
     */
    private static SecretKey getSecretKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
