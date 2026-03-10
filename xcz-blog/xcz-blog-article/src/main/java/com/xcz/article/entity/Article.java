package com.xcz.article.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 文章实体类 (MongoDB 文档)
 *
 * @author xcz
 */
@Data
@Document(collection = "article")
public class Article {

    /**
     * 主键 ID (MongoDB 的 ObjectId)
     */
    @Id
    private String id;

    /**
     * 文章标题
     */
    @Field("title")
    @Indexed
    private String title;

    /**
     * 文章内容
     */
    @Field("content")
    private String content;

    /**
     * 作者 ID
     */
    @Field("author_id")
    @Indexed
    private Long authorId;

    /**
     * 作者名称
     */
    @Field("author_name")
    private String authorName;

    /**
     * 分类 ID
     */
    @Field("category_id")
    @Indexed
    private String categoryId;

    /**
     * 分类名称
     */
    @Field("category_name")
    private String categoryName;

    /**
     * 标签列表
     */
    @Field("tags")
    private java.util.List<String> tags;

    /**
     * 浏览次数
     */
    @Field("view_count")
    private Long viewCount = 0L;

    /**
     * 点赞次数
     */
    @Field("like_count")
    private Long likeCount = 0L;

    /**
     * 评论次数
     */
    @Field("comment_count")
    private Long commentCount = 0L;

    /**
     * 创建时间
     */
    @CreatedDate
    @Field("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Field("update_time")
    private LocalDateTime updateTime;
}
