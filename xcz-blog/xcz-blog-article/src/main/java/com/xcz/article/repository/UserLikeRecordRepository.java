package com.xcz.article.repository;

import com.xcz.article.entity.UserLikeRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户点赞记录 Repository
 *
 * @author xcz
 */
@Repository
public interface UserLikeRecordRepository extends MongoRepository<UserLikeRecord, String> {

    /**
     * 根据用户 ID 和文章 ID 查询点赞记录
     */
    Optional<UserLikeRecord> findByUserIdAndArticleId(Long userId, String articleId);

    /**
     * 根据用户 ID 查询点赞记录列表
     */
    List<UserLikeRecord> findByUserId(Long userId);

    /**
     * 根据文章 ID 查询点赞记录列表
     */
    List<UserLikeRecord> findByArticleId(String articleId);

    /**
     * 根据用户 ID 和文章 ID 删除点赞记录
     */
    void deleteByUserIdAndArticleId(Long userId, String articleId);
}
