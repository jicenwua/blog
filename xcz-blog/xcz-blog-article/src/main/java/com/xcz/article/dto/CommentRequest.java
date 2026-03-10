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
     * 评论内容
     */
    private String content;
}
