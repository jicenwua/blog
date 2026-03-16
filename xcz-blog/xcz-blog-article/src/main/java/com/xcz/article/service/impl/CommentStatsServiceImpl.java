package com.xcz.article.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.xcz.article.entity.Comment;
import com.xcz.article.service.CommentStatsService;
import com.xcz.common.cache.extend.RedissonClientEnum;
import com.xcz.common.cache.utils.RedissonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.redisson.api.RKeys;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;

/**
 * 评论统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentStatsServiceImpl implements CommentStatsService {

    private final MongoTemplate mongoTemplate;

    private static final String COMMENT_LIKE_COUNT_PREFIX = "comment:like:";
    private static final String USER_COMMENT_LIKE_PREFIX = "user:comment:like:";
    private static final RedissonClient redisson = RedissonUtil.getRedissonClient(RedissonClientEnum.DATABASE_1);

    @Override
    public void incrementLikeCount(String commentId, Long userId) {
        String likeCountKey = COMMENT_LIKE_COUNT_PREFIX + commentId;
        String userLikeKey = USER_COMMENT_LIKE_PREFIX + userId;

        // 记录用户点赞
        redisson.getSet(userLikeKey).add(commentId);
        // 记录评论被点赞的用户
        redisson.getSet(likeCountKey).add(userId);

        log.debug("评论 {} 点赞数 +1，用户：{}", commentId, userId);
    }

    @Override
    public void decrementLikeCount(String commentId, Long userId) {
        String likeCountKey = COMMENT_LIKE_COUNT_PREFIX + commentId;
        String userLikeKey = USER_COMMENT_LIKE_PREFIX + userId;

        // 删除用户点赞记录
        redisson.getSet(userLikeKey).remove(commentId);
        // 从评论点赞列表中移除用户
        redisson.getSet(likeCountKey).remove(userId);

        log.debug("评论 {} 点赞数 -1，用户：{}", commentId, userId);
    }

    @Override
    public boolean hasLiked(String commentId, Long userId) {
        if (userId == null) {
            return false;
        }

        String userLikeKey = USER_COMMENT_LIKE_PREFIX + userId;
        return redisson.getSet(userLikeKey).contains(commentId);
    }

    @Override
    public long getLikeCount(String commentId) {
        // 先从 MongoDB 获取基础值
        Comment comment = mongoTemplate.findById(commentId, Comment.class);
        long mongoCount = comment != null && comment.getLikeCount() != null ? comment.getLikeCount() : 0L;

        // 加上 Redis 中的增量
        String likeCountKey = COMMENT_LIKE_COUNT_PREFIX + commentId;
        RSet<Object> rSet = redisson.getSet(likeCountKey);
        long redisCount = rSet.size();

        return mongoCount + redisCount;
    }

    /**
     * 定时任务：每 5 分钟同步一次 Redis 数据到 MongoDB
     */
    @Scheduled(fixedRate = 300000) // 5 分钟
    @Override
    public void syncToMongoDB() {
        log.info("开始同步评论统计数据到 MongoDB...");

        try {
            RKeys rKeys = redisson.getKeys();
            Iterable<String> keys = rKeys.getKeysByPattern(COMMENT_LIKE_COUNT_PREFIX + "*");

            for (String key : keys) {
                try {
                    String commentId = key.replace(COMMENT_LIKE_COUNT_PREFIX, "");
                    RSet<Object> rSet = redisson.getSet(key);
                    long count = rSet.size();

                    if (count > 0) {
                        // 更新 MongoDB 中的点赞数
                        Query query = new Query(Criteria.where("_id").is(commentId));
                        Update update = new Update().inc("likeCount", count);
                        UpdateResult result = mongoTemplate.updateFirst(query, update, Comment.class);

                        if (result.getMatchedCount() > 0) {
                            log.debug("同步评论点赞数：{}, 数量：{}", commentId, count);
                            // 清空 Redis 计数
                            rSet.clear();
                        }
                    }
                } catch (Exception e) {
                    log.error("处理评论点赞数同步失败，key: {}", key, e);
                }
            }

            log.info("评论统计数据同步完成");
        } catch (Exception e) {
            log.error("同步评论统计数据失败", e);
        }
    }
}
