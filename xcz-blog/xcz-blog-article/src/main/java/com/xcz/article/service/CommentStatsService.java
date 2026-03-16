package com.xcz.article.service;

/**
 * 评论统计服务接口
 */
public interface CommentStatsService {

    /**
     * 增加评论点赞数（Redis 缓存）
     * @param commentId 评论 ID
     * @param userId 用户 ID
     */
    void incrementLikeCount(String commentId, Long userId);

    /**
     * 取消评论点赞（Redis 缓存）
     * @param commentId 评论 ID
     * @param userId 用户 ID
     */
    void decrementLikeCount(String commentId, Long userId);

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论 ID
     * @param userId 用户 ID
     * @return true-已点赞，false-未点赞
     */
    boolean hasLiked(String commentId, Long userId);

    /**
     * 获取评论点赞数
     * @param commentId 评论 ID
     * @return 点赞数
     */
    long getLikeCount(String commentId);

    /**
     * 同步所有评论的统计数据到 MongoDB
     */
    void syncToMongoDB();
}
