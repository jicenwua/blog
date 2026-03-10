package com.xcz.common.core.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密解密工具类
 */
public class CryptoUtils {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SHA256_ALGORITHM = "SHA-256";

    /**
     * AES加密
     *
     * @param data 待加密数据
     * @param key  密钥(Base64编码)
     * @param iv   初始化向量(Base64编码)
     * @return 加密后的数据(Base64编码)
     */
    public static String aesEncrypt(String data, String key, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES解密
     *
     * @param encryptedData 加密数据(Base64编码)
     * @param key          密钥(Base64编码)
     * @param iv           初始化向量(Base64编码)
     * @return 解密后的数据
     */
    public static String aesDecrypt(String encryptedData, String key, String iv) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * SHA-256哈希
     *
     * @param data 待哈希数据
     * @return 哈希值
     */
    public static String sha256Hash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA256_ALGORITHM);
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256哈希失败", e);
        }
    }

    /**
     * 生成随机AES密钥
     *
     * @return Base64编码的密钥
     */
    public static String generateAesKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(256); // AES-256
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("生成AES密钥失败", e);
        }
    }

    /**
     * 生成随机IV
     *
     * @return Base64编码的IV
     */
    public static String generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }
}
