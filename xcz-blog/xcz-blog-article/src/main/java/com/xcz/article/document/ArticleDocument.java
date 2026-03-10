package com.xcz.article.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章 Elasticsearch 文档
 *
 * @author xcz
 */
@Data
@Document(indexName = "article")
public class ArticleDocument {

    /**
     * 主键 ID (与 MongoDB 的 ID 一致)
     */
    @Id
    private String id;

    /**
     * 文章标题 (使用 ik_max_word 分词，支持中文分词)
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    /**
     * 文章内容 (使用 ik_max_word 分词)
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * 标签列表 (使用 keyword 类型，不分词)
     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

    /**
     * 分类名称
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 作者 ID
     */
    @Field(type = FieldType.Long)
    private Long authorId;

    /**
     * 作者名称
     */
    @Field(type = FieldType.Keyword)
    private String authorName;

    /**
     * 浏览次数
     */
    @Field(type = FieldType.Long)
    private Long viewCount;

    /**
     * 点赞次数
     */
    @Field(type = FieldType.Long)
    private Long likeCount;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updateTime;
}
