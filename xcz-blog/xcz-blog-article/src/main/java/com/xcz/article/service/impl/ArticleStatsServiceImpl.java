package com.xcz.article.service.impl;

import com.xcz.article.dto.ArticleSyncMessage;
import com.xcz.article.entity.Article;
import com.xcz.article.entity.UserLikeRecord;
import com.xcz.article.repository.ArticleRepository;
import com.xcz.article.repository.UserLikeRecordRepository;
import com.xcz.article.service.ArticleStatsService;
import com.xcz.common.cache.extend.RedissonClientEnum;
import com.xcz.common.cache.utils.RedissonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 文章统计服务实现类（基于 Redis 缓存）
 *
 * @author xcz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleStatsServiceImpl implements ArticleStatsService {

    private final UserLikeRecordRepository userLikeRecordRepository;
    private final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    private static final String VIEW_COUNT_PREFIX = "article:view";
    private static final String LIKE_COUNT_PREFIX = "article:like";
    private static final String USER_LIKE_PREFIX = "user:like:";
    private static final RedissonClient redisson = RedissonUtil.getRedissonClient(RedissonClientEnum.DATABASE_1);

    private Supplier<RMapCache<String,Set<String>>> viewRedisson = () -> redisson.getMapCache(VIEW_COUNT_PREFIX);
    private Supplier<RMapCache<String,Set<String>>> likeRedisson = () -> redisson.getMapCache(LIKE_COUNT_PREFIX);
    private Supplier<RSet<String>> userLike = () -> redisson.getSet(USER_LIKE_PREFIX);

    @Override
    public void incrementViewCount(String articleId, Long userId, String ipAddress) {

        String viewCountKey = VIEW_COUNT_PREFIX + articleId;

        String customer = userId != null ? userId.toString() : ipAddress;
        redisson.getSet(viewCountKey).add(customer);
        log.debug("文章 {} 浏览量 +1，访客：{}", articleId, customer);
    }

    @Override
    public void incrementLikeCount(String articleId, Long userId) {
        String likeCountKey = LIKE_COUNT_PREFIX + articleId;
        String userLikeKey = USER_LIKE_PREFIX + userId;

        redisson.getSet(userLikeKey).add(articleId);
        redisson.getSet(likeCountKey).add(userId);

        log.debug("文章 {} 点赞数 +1，用户：{}", articleId, userId);
    }

    @Override
    public void decrementLikeCount(String articleId, Long userId) {
        String likeCountKey = LIKE_COUNT_PREFIX + articleId;
        String userLikeKey = USER_LIKE_PREFIX + userId;

        redisson.getSet(userLikeKey).remove(articleId);
        redisson.getSet(likeCountKey).remove(articleId);

        log.debug("文章 {} 点赞数 -1，用户：{}", articleId, userId);
    }

    @Override
    public boolean hasLiked(String articleId, Long userId) {
        if (userId == null) {
            return false;
        }

        String userLikeKey = USER_LIKE_PREFIX + userId;

        return redisson.getSet(userLikeKey).contains(articleId);
    }

    /**
     * 定时任务：每 5 分钟同步一次 Redis 数据到 MongoDB
     */
    @Scheduled(fixedRate = 120000) // 5 分钟
    @Override
    public void syncToMongoDB() {
        log.info("开始同步文章统计数据到 MongoDB...");

        try {
            // 获取所有文章的浏览量和点赞数
            syncViewCounts();
            syncLikeCounts();

            log.info("文章统计数据同步完成");
        } catch (Exception e) {
            log.error("同步文章统计数据失败", e);
        }
    }

    /**
     * 同步浏览量：Redis -> MongoDB -> (MQ) -> ES
     */
    public void syncViewCounts() {
        RKeys rKeys = redisson.getKeys();
        Iterable<String> keys = rKeys.getKeysByPattern(VIEW_COUNT_PREFIX + "*");
        log.info("浏览量keys：{}", keys);
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Article.class);
        boolean hasData = false;

        for (String key : keys) {
            try {
                String articleId = key.replace(VIEW_COUNT_PREFIX, "");
                RSet<Object> setCache = redisson.getSet(key);
                long count = setCache.size();
                log.info("更新：{}，浏览量：{}", articleId, count);

                if (count >= 0) {
                    // 更新 MongoDB - 在原有基础上累加
                    Query query = new Query(Criteria.where("_id").is(articleId));
                    Update update = new Update().inc("viewCount", count);
                    bulkOps.updateOne(query, update);
                    hasData = true;
                    setCache.clear();
                }
            } catch (Exception e) {
                log.error("处理浏览量同步失败, key: {}", key, e);
            }
        }

        if (hasData) {
            bulkOps.execute();
            log.info("已批量同步浏览量至 MongoDB");
        }
    }

    /**
     * 同步点赞数：Redis -> MongoDB -> (MQ) -> ES
     */
    public void syncLikeCounts() {
        RKeys rKeys = redisson.getKeys();
        Iterable<String> keys = rKeys.getKeysByPattern(LIKE_COUNT_PREFIX + "*");

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Article.class);
        boolean hasData = false;

        for (String key : keys) {
            try {
                String articleId = key.replace(LIKE_COUNT_PREFIX, "");
                RSet<Object> rSet = redisson.getSet(key);
                long count = rSet.size();

                if (count >= 0) {
                    Query query = new Query(Criteria.where("_id").is(articleId));
                    Update update = new Update().inc("likeCount", count);
                    bulkOps.updateOne(query, update);
                    hasData = true;
                    rSet.clear();
                }
            } catch (Exception e) {
                log.error("处理点赞数同步失败, key: {}", key, e);
            }
        }

        if (hasData) {
            bulkOps.execute();
            log.info("已批量同步点赞数至 MongoDB");
        }
    }



    @Override
    public long getViewCount(String articleId) {
        // 直接从 MongoDB 获取浏览量
        Article article = mongoTemplate.findById(articleId, Article.class);
        return article != null && article.getViewCount() != null ? article.getViewCount() : 0L;
    }

    @Override
    public long getLikeCount(String articleId) {
        // 直接从 MongoDB 获取点赞数
        Article article = mongoTemplate.findById(articleId, Article.class);
        return article != null && article.getLikeCount() != null ? article.getLikeCount() : 0L;
    }

    /**
     * 保存用户点赞记录到 MongoDB
     */
    public void saveUserLikeRecord(Long userId, String articleId) {
        // 先检查是否已存在
        if (userLikeRecordRepository.findByUserIdAndArticleId(userId, articleId).isPresent()) {
            return;
        }

        UserLikeRecord record = new UserLikeRecord();
        record.setUserId(userId);
        record.setArticleId(articleId);
        record.setCreateTime(LocalDateTime.now());

        userLikeRecordRepository.save(record);
        log.debug("保存用户点赞记录：用户 {}, 文章 {}", userId, articleId);
    }

    /**
     * 删除用户点赞记录
     */
    public void deleteUserLikeRecord(Long userId, String articleId) {
        userLikeRecordRepository.deleteByUserIdAndArticleId(userId, articleId);
        log.debug("删除用户点赞记录：用户 {}, 文章 {}", userId, articleId);
    }
}
