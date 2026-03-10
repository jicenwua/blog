package com.xcz.article.dto;

import lombok.Data;

import java.util.List;

/**
 * 文章发布请求 DTO
 *
 * @author xcz
 */
@Data
public class ArticlePublishRequest {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 分类 ID
     */
    private String categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签列表
     */
    private List<String> tags;
}
