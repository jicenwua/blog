package com.xcz.article.repository.es;

import com.xcz.article.document.ArticleDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章 Elasticsearch Repository
 *
 * @author xcz
 */
@Repository
public interface ArticleEsRepository extends ElasticsearchRepository<ArticleDocument, String> {

    /**
     * 根据作者 ID 删除文章
     */
    void deleteByAuthorId(Long authorId);

    /**
     * 根据分类名称删除文章
     */
    void deleteByCategoryName(String categoryName);

    /**
     * 自定义查询：根据关键词搜索文章（标题、标签、内容）
     * 按匹配程度排序：标题 > 标签 > 内容
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^3\", \"tags^2\", \"content\"], \"type\": \"best_fields\"}}")
    Page<ArticleDocument> searchArticles(String keyword, Pageable pageable);

    /**
     * 根据标题搜索
     */
    List<ArticleDocument> findByTitleContaining(String title);

    /**
     * 根据标签搜索
     */
    List<ArticleDocument> findByTagsContaining(String tag);
}
