package com.xcz.article.dto;

import lombok.Data;

/**
 * 评论请求 DTO
 *
 * @author xcz
 */
@Data
public class CommentRequest {

    /**
     * 文章 ID
     */
    private String articleId;

    /**
     * 父评论 ID (如果是回复评论则必填)
     */
    private String parentCommentId;

    /**
     * 被回复用户的昵称 (如果是回复评论则必填)
     */
    private String parentUserName;

    /**
     * 评论内容
     */
    private String content;
}
