package com.xcz.article.service;

import com.xcz.article.dto.CategoryStats;
import com.xcz.article.dto.TagCount;
import com.xcz.article.entity.Article;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * 文章服务接口
 *
 * @author xcz
 */
public interface ArticleService {

    /**
     * 保存文章
     * @param article 文章对象
     * @return 保存后的文章
     */
    Article save(Article article);

    /**
     * 根据 ID 查询文章
     * @param id 文章 ID
     * @return 文章对象
     */
    Optional<Article> findById(String id);

    /**
     * 查询所有文章
     * @return 文章列表
     */
    List<Article> findAll();

    /**
     * 分页查询文章
     * @param page 页码 (从 0 开始)
     * @param size 每页大小
     * @return 分页结果
     */
    Page<Article> findByPage(int page, int size);

    /**
     * 根据作者 ID 查询文章
     * @param authorId 作者 ID
     * @return 文章列表
     */
    List<Article> findByAuthorId(Long authorId);

    /**
     * 分页查询文章（按观看数量排序）
     * @param page 页码 (从 0 开始)
     * @param size 每页大小
     * @return 分页结果
     */
    Page<Article> findAllWithSort(int page, int size);

    /**
     * 根据分类 ID 分页查询文章
     * @param categoryId 分类 ID
     * @param page 页码 (从 0 开始)
     * @param size 每页大小
     * @return 分页结果
     */
    Page<Article> findByCategoryId(String categoryId, int page, int size);

    /**
     * 根据分类 ID 查询文章
     * @param categoryId 分类 ID
     * @return 文章列表
     */
    List<Article> findByCategoryId(String categoryId);

    /**
     * 根据标题模糊查询
     * @param title 标题关键词
     * @return 文章列表
     */
    List<Article> searchByTitle(String title);

    /**
     * 根据标签查询
     * @param tag 标签名称
     * @return 文章列表
     */
    List<Article> findByTag(String tag);

    /**
     * 增加浏览次数
     * @param id 文章 ID
     */
    void incrementViewCount(String id);

    /**
     * 增加点赞次数
     * @param id 文章 ID
     */
    void incrementLikeCount(String id);

    /**
     * 删除文章
     * @param id 文章 ID
     */
    void deleteById(String id);

    /**
     * 统计文章总数
     * @return 文章总数
     */
    long count();

    /**
     * 统计作者的文章数量
     * @param authorId 作者 ID
     * @return 文章数量
     */
    Long countByAuthorId(Long authorId);

    /**
     * 统计分类下的文章数量
     * @param categoryId 分类 ID
     * @return 文章数量
     */
    Long countByCategoryId(String categoryId);

    /**
     * 查询热门文章 (按浏览量排序)
     * @param limit 返回数量限制
     * @return 热门文章列表
     */
    List<Article> findTopArticles(int limit);

    /**
     * 获取总浏览量
     * @return 总浏览量
     */
    long getTotalViewCount();

    /**
     * 获取总评论数
     * @return 总评论数
     */
    long getTotalCommentCount();

    /**
     * 获取所有分类的统计数据（文章数量和浏览量）
     * @return 分类统计列表
     */
    List<CategoryStats> getCategoryStatistics();

    /**
     * 获取热门标签（按数量排序）
     * @param limit 返回数量限制
     * @return 热门标签列表
     */
    List<TagCount> getHotTags(int limit);
}
