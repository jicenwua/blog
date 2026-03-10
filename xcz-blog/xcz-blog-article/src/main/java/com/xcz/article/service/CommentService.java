package com.xcz.article.service;

import com.xcz.article.dto.CommentRequest;
import com.xcz.article.dto.CommentResponse;
import org.springframework.data.domain.Page;

/**
 * 评论服务接口
 * 
 * @author xcz
 */
public interface CommentService {

    /**
     * 发表评论 (包括文章评论和楼层回复)
     * @param request 评论请求
     * @param userId 用户 ID
     * @param userNickname 用户昵称
     * @param userAvatar 用户头像
     * @return 评论响应
     */
    CommentResponse addComment(CommentRequest request, Long userId, String userNickname, String userAvatar);

    /**
     * 获取文章的评论列表 (分页，只包含一级评论及其回复)
     * @param articleId 文章 ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    Page<CommentResponse> getArticleComments(String articleId, int page, int size);

    /**
     * 获取评论的回复列表
     * @param parentCommentId 父评论 ID
     * @return 回复列表
     */
    java.util.List<CommentResponse> getCommentReplies(String parentCommentId);

    /**
     * 点赞评论
     * @param commentId 评论 ID
     */
    void likeComment(String commentId);

    /**
     * 删除评论
     * @param commentId 评论 ID
     */
    void deleteComment(String commentId);

    /**
     * 统计文章的评论数量
     * @param articleId 文章 ID
     * @return 评论数量
     */
    Long countArticleComments(String articleId);

    /**
     * 管理后台 - 分页查询所有评论
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    Page<CommentResponse> getAllComments(int page, int size);

    /**
     * 删除文章的所有评论
     * @param articleId 文章 ID
     */
    void deleteCommentsByArticleId(String articleId);
}
