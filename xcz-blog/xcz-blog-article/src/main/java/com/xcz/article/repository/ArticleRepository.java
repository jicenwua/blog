package com.xcz.article.repository;

import com.xcz.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章 Repository 接口
 *
 * @author xcz
 */
@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {

    /**
     * 根据作者 ID 查询文章列表
     */
    List<Article> findByAuthorId(Long authorId);

    /**
     * 根据分类 ID 查询文章列表（分页）
     */
    Page<Article> findByCategoryId(String categoryId, Pageable pageable);

    /**
     * 根据分类 ID 查询文章列表
     */
    List<Article> findByCategoryId(String categoryId);

    /**
     * 根据标题模糊查询
     */
    List<Article> findByTitleContaining(String title);

    /**
     * 根据标签查询
     */
    List<Article> findByTagsContaining(String tag);

    /**
     * 分页查询文章
     */
    Page<Article> findAll(Pageable pageable);

    /**
     * 统计作者的文章数量
     */
    Long countByAuthorId(Long authorId);

    /**
     * 统计分类下的文章数量
     */
    Long countByCategoryId(String categoryId);
}
