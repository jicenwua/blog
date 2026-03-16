package com.xcz.article.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 评论实体类 (MongoDB 文档)
 * 
 * @author xcz
 */
@Data
@Document(collection = "comment")
public class Comment {

    /**
     * 主键 ID (MongoDB 的 ObjectId)
     */
    @Id
    private String id;

    /**
     * 评论类型：1-文章评论，2-楼层评论 (回复其他评论)
     */
    @Field("comment_type")
    private Integer commentType;

    /**
     * 文章 ID (如果是文章评论)
     */
    @Field("article_id")
    @Indexed
    private String articleId;

    /**
     * 父评论 ID (如果是楼层评论，指向被回复的评论)
     */
    @Field("parent_comment_id")
    @Indexed
    private String parentCommentId;

    /**
     * 被回复用户的昵称 (用于显示回复时的@对象)
     */
    @Field("parent_user_name")
    private String parentUserName;

    /**
     * 评论的用户 ID
     */
    @Field("user_id")
    @Indexed
    private Long userId;

    /**
     * 用户昵称
     */
    @Field("user_nickname")
    private String userNickname;

    /**
     * 用户头像
     */
    @Field("user_avatar")
    private String userAvatar;

    /**
     * 评论内容
     */
    @Field("content")
    private String content;

    /**
     * 点赞次数
     */
    @Field("like_count")
    private Long likeCount = 0L;

    /**
     * 回复数量 (该评论有多少条回复)
     */
    @Field("reply_count")
    private Long replyCount = 0L;

    /**
     * 创建时间
     */
    @CreatedDate
    @Field("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Field("update_time")
    private LocalDateTime updateTime;
}
