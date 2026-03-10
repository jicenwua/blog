package com.xcz.article.service;

/**
 * 文章统计服务（基于 Redis 缓存）
 *
 * @author xcz
 */
public interface ArticleStatsService {

    /**
     * 增加文章浏览量（Redis 缓存）
     * @param articleId 文章 ID
     * @param userId 用户 ID（可能为 null）
     * @param ipAddress IP 地址
     */
    void incrementViewCount(String articleId, Long userId, String ipAddress);

    /**
     * 增加文章点赞数（Redis 缓存）
     * @param articleId 文章 ID
     * @param userId 用户 ID
     */
    void incrementLikeCount(String articleId, Long userId);

    /**
     * 取消文章点赞（Redis 缓存）
     * @param articleId 文章 ID
     * @param userId 用户 ID
     */
    void decrementLikeCount(String articleId, Long userId);

    /**
     * 检查用户是否已点赞
     * @param articleId 文章 ID
     * @param userId 用户 ID
     * @return true-已点赞，false-未点赞
     */
    boolean hasLiked(String articleId, Long userId);

    /**
     * 获取文章浏览量
     * @param articleId 文章 ID
     * @return 浏览量
     */
    long getViewCount(String articleId);

    /**
     * 获取文章点赞数
     * @param articleId 文章 ID
     * @return 点赞数
     */
    long getLikeCount(String articleId);

    /**
     * 同步所有文章的统计数据的到 MongoDB
     */
    void syncToMongoDB();
}
