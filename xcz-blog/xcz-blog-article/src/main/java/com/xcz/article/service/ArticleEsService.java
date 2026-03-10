package com.xcz.article.service;

import com.xcz.article.document.ArticleDocument;
import org.springframework.data.domain.Page;

/**
 * 文章 Elasticsearch 服务
 *
 * @author xcz
 */
public interface ArticleEsService {

    /**
     * 搜索文章
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 搜索结果分页
     */
    Page<ArticleDocument> searchArticles(String keyword, int page, int size);

    /**
     * 同步文章到 Elasticsearch
     * @param document 文章文档
     */
    void syncArticle(ArticleDocument document);

    /**
     * 从 Elasticsearch 删除文章
     * @param id 文章 ID
     */
    void deleteArticle(String id);

}
