package com.xcz.article.repository;

import com.xcz.article.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论 Repository 接口
 *
 * @author xcz
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    /**
     * 根据文章 ID 查询评论列表 (只查询一级评论)
     */
    Page<Comment> findByArticleIdAndCommentType(String articleId, Integer commentType, Pageable pageable);

    /**
     * 根据父评论 ID 查询回复列表
     */
    List<Comment> findByParentCommentIdOrderByCreateTimeAsc(String parentCommentId);

    /**
     * 根据用户 ID 查询评论
     */
    List<Comment> findByUserId(Long userId);

    /**
     * 统计文章的评论数量
     */
    Long countByArticleIdAndCommentType(String articleId, Integer commentType);

    /**
     * 删除文章的所有评论
     */
    void deleteByArticleId(String articleId);

    /**
     * 查询评论的回复数量
     */
    Long countByParentCommentId(String parentCommentId);

    /**
     * 分页查询所有评论（按创建时间倒序）
     */
    Page<Comment> findAllByOrderByCreateTimeDesc(Pageable pageable);
}
