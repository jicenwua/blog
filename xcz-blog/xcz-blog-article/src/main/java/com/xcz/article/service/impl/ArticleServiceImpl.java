package com.xcz.article.service.impl;

import com.xcz.article.dto.CategoryStats;
import com.xcz.article.dto.TagCount;
import com.xcz.article.entity.Article;
import com.xcz.article.repository.ArticleRepository;
import com.xcz.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author xcz
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Optional<Article> findById(String id) {
        return articleRepository.findById(id);
    }

    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Override
    public Page<Article> findByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return articleRepository.findAll(pageable);
    }

    @Override
    public Page<Article> findAllWithSort(int page, int size) {
        // 按观看数量降序排序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        return articleRepository.findAll(pageable);
    }

    @Override
    public Page<Article> findByCategoryId(String categoryId, int page, int size) {
        // 按创建时间降序排序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return articleRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public List<Article> findByAuthorId(Long authorId) {
        return articleRepository.findByAuthorId(authorId);
    }

    @Override
    public List<Article> findByCategoryId(String categoryId) {
        return articleRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Article> searchByTitle(String title) {
        return articleRepository.findByTitleContaining(title);
    }

    @Override
    public List<Article> findByTag(String tag) {
        return articleRepository.findByTagsContaining(tag);
    }

    @Override
    public void incrementViewCount(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().inc("view_count", 1);
        mongoTemplate.updateFirst(query, update, Article.class);
    }

    @Override
    public void incrementLikeCount(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().inc("like_count", 1);
        mongoTemplate.updateFirst(query, update, Article.class);
    }

    @Override
    public void deleteById(String id) {
        articleRepository.deleteById(id);
    }

    @Override
    public long count() {
        return articleRepository.count();
    }

    @Override
    public Long countByAuthorId(Long authorId) {
        return articleRepository.countByAuthorId(authorId);
    }

    @Override
    public Long countByCategoryId(String categoryId) {
        return articleRepository.countByCategoryId(categoryId);
    }

    @Override
    public List<Article> findTopArticles(int limit) {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "view_count"))
                .limit(limit);
        return mongoTemplate.find(query, Article.class);
    }

    @Override
    public long getTotalViewCount() {
        // 使用 MongoDB 聚合管道计算总浏览量
        org.springframework.data.mongodb.core.aggregation.Aggregation agg = 
            org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation(
                org.springframework.data.mongodb.core.aggregation.Aggregation.group().sum("view_count").as("total")
            );
        
        org.springframework.data.mongodb.core.aggregation.AggregationResults<org.bson.Document> results = 
            mongoTemplate.aggregate(agg, "article", org.bson.Document.class);
        
        org.bson.Document doc = results.getUniqueMappedResult();
        return doc != null && doc.get("total") != null ? doc.getLong("total") : 0;
    }

    @Override
    public long getTotalCommentCount() {
        // 使用 MongoDB 聚合管道计算总评论数
        org.springframework.data.mongodb.core.aggregation.Aggregation agg = 
            org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation(
                org.springframework.data.mongodb.core.aggregation.Aggregation.group().sum("comment_count").as("total")
            );
        
        org.springframework.data.mongodb.core.aggregation.AggregationResults<org.bson.Document> results = 
            mongoTemplate.aggregate(agg, "article", org.bson.Document.class);
        
        org.bson.Document doc = results.getUniqueMappedResult();
        return doc != null && doc.get("total") != null ? doc.getLong("total") : 0;
    }

    @Override
    public List<CategoryStats> getCategoryStatistics() {
        // 获取所有文章
        List<Article> articles = articleRepository.findAll();
        
        // 按分类分组统计
        Map<String, List<Article>> articlesByCategory = articles.stream()
            .filter(article -> article.getCategoryName() != null && !article.getCategoryName().isEmpty())
            .collect(Collectors.groupingBy(Article::getCategoryName));
        
        // 转换为 CategoryStats 列表
        return articlesByCategory.entrySet().stream()
            .map(entry -> new CategoryStats(
                entry.getKey(),
                (long) entry.getValue().size(),
                entry.getValue().stream()
                    .mapToLong(article -> article.getViewCount() != null ? article.getViewCount() : 0)
                    .sum()
            ))
            .sorted((a, b) -> b.getArticleCount().compareTo(a.getArticleCount()))
            .collect(Collectors.toList());
    }

    @Override
    public List<TagCount> getHotTags(int limit) {
        // 获取所有文章
        List<Article> articles = articleRepository.findAll();
        
        // 统计所有标签的出现次数（忽略大小写）
        Map<String, Long> tagCounts = articles.stream()
            .filter(article -> article.getTags() != null && !article.getTags().isEmpty())
            .flatMap(article -> article.getTags().stream())
            .filter(tag -> tag != null && !tag.trim().isEmpty())
            .collect(Collectors.groupingBy(
                String::toLowerCase,
                Collectors.counting()
            ));
        
        // 转换为 TagCount 列表并排序
        return tagCounts.entrySet().stream()
            .map(entry -> new TagCount(entry.getKey(), entry.getValue()))
            .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
            .limit(limit)
            .collect(Collectors.toList());
    }
}
