package com.xcz.article.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 用户点赞记录实体类
 *
 * @author xcz
 */
@Data
@Document(collection = "user_like_record")
public class UserLikeRecord {

    /**
     * 主键 ID
     */
    @Id
    private String id;

    /**
     * 用户 ID
     */
    @Field("user_id")
    @Indexed
    private Long userId;

    /**
     * 点赞的文章 ID
     */
    @Field("article_id")
    @Indexed
    private String articleId;

    /**
     * 点赞时间
     */
    @CreatedDate
    @Field("create_time")
    private LocalDateTime createTime;
}
