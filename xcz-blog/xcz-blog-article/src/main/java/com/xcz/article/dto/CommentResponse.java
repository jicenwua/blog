package com.xcz.article.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应 DTO (包含回复列表)
 * 
 * @author xcz
 */
@Data
public class CommentResponse {

    /**
     * 评论 ID
     */
    private String id;

    /**
     * 评论类型：1-文章评论，2-楼层评论
     */
    private Integer commentType;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞次数
     */
    private Long likeCount;

    /**
     * 回复数量
     */
    private Long replyCount;

    /**
     * 被回复用户的昵称 (仅楼层评论包含)
     */
    private String parentUserName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 回复列表 (仅一级评论包含)
     */
    private List<CommentResponse> replies;
}
