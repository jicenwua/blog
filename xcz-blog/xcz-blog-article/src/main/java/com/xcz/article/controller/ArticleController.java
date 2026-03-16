package com.xcz.article.controller;

import com.xcz.article.document.ArticleDocument;
import com.xcz.article.dto.ArticlePublishRequest;
import com.xcz.article.dto.ArticleSyncMessage;
import com.xcz.article.dto.CategoryStats;
import com.xcz.article.dto.TagCount;
import com.xcz.article.entity.Article;
import com.xcz.article.mq.producer.ArticleSyncProducer;
import com.xcz.article.repository.ArticleRepository;
import com.xcz.article.service.ArticleEsService;
import com.xcz.article.service.ArticleService;
import com.xcz.article.service.ArticleStatsService;
import com.xcz.article.service.CommentService;
import com.xcz.article.service.ImageService;
import com.xcz.common.core.constant.SecurityConstants;
import com.xcz.common.core.domain.ResponseEntity;
import com.xcz.common.core.utils.response.ResponseEntityUtils;
import com.xcz.common.security.extend.LoginUser;
import com.xcz.common.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 文章控制器
 *
 * @author xcz
 */
@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleSyncProducer articleSyncProducer;
    private final ArticleEsService articleEsService;
    private final ArticleRepository articleRepository;
    private final ArticleStatsService articleStatsService;
    private final ImageService imageService;
    private final CommentService commentService;

    /**
     * 发布文章
     */
    @PostMapping("/publish")
    public ResponseEntity<Article> publish(@RequestBody ArticlePublishRequest request) {
        try {
            // 参数校验
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ResponseEntityUtils.fail("文章标题不能为空");
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntityUtils.fail("文章内容不能为空");
            }
            if (request.getCategoryId() == null || request.getCategoryId().trim().isEmpty()) {
                return ResponseEntityUtils.fail("分类 ID 不能为空");
            }

            // 转换为实体对象
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Article article = new Article();
            article.setTitle(request.getTitle());
            article.setContent(request.getContent());
            article.setAuthorId(loginUser.getUserId());
            article.setAuthorName(loginUser.getName());
            article.setCategoryId(request.getCategoryId());
            article.setCategoryName(request.getCategoryName());
            article.setTags(request.getTags());

            // 保存文章
            Article saved = articleService.save(article);

            // 发送消息到 RocketMQ，同步到 Elasticsearch
            ArticleSyncMessage syncMessage = convertToSyncMessage(saved);
            articleSyncProducer.sendArticleCreateMessage(syncMessage);

            return ResponseEntityUtils.ok(saved);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("发布失败：" + e.getMessage());
        }
    }

    /**
     * 增加浏览次数（基于 Redis）
     */
    @PostMapping("/view/{id}")
    public ResponseEntity<Void> incrementView(@PathVariable String id) {
        try {
            // 获取用户信息或 IP
            Long userId = null;
            String ipAddress = "127.0.0.1"; // TODO: 从请求中获取真实 IP

            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null) {
                    userId = loginUser.getUserId();
                }
            } catch (Exception e) {
                // 未登录，使用 IP
            }

            // 增加 Redis 缓存中的浏览量
            articleStatsService.incrementViewCount(id, userId, ipAddress);

            return ResponseEntityUtils.ok();
        } catch (Exception e) {
            return ResponseEntityUtils.fail("操作失败：" + e.getMessage());
        }
    }

    /**
     * 点赞文章（基于 Redis）
     */
    @PostMapping("/like/{id}")
    public ResponseEntity<Void> likeArticle(@PathVariable String id) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();

            // 检查是否已点赞
            if (articleStatsService.hasLiked(id, userId)) {
                // 取消点赞
                articleStatsService.decrementLikeCount(id, userId);
            } else {
                // 增加点赞
                articleStatsService.incrementLikeCount(id, userId);
            }

            return ResponseEntityUtils.ok();
        } catch (Exception e) {
            return ResponseEntityUtils.fail("操作失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否已点赞
     */
    @GetMapping("/liked/{id}")
    public ResponseEntity<Boolean> checkLiked(@PathVariable String id) {
        try {
            Long userId = null;
            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                userId = loginUser.getUserId();
            } catch (Exception e) {
                // 未登录
            }

            boolean liked = articleStatsService.hasLiked(id, userId);
            return ResponseEntityUtils.ok(liked);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取文章点赞数
     */
    @GetMapping("/likes/{id}")
    public ResponseEntity<Long> getLikeCount(@PathVariable String id) {
        try {
            long likeCount = articleStatsService.getLikeCount(id);
            return ResponseEntityUtils.ok(likeCount);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据 ID 获取文章详情
     */
    @GetMapping("/xq/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        try {
            Article article = articleService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
            return ResponseEntityUtils.ok(article);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取文章列表 (分页，按观看数量排序，支持按分类筛选)
     */
    @GetMapping("/list")
    public ResponseEntity<Page<Article>> getArticleList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryId
    ) {
        try {
            Page<Article> articles;
            if (categoryId != null && !categoryId.isEmpty()) {
                // 按分类筛选
                articles = articleService.findByCategoryId(categoryId, page, size);
            } else {
                // 获取全部文章，按观看数量排序
                articles = articleService.findAllWithSort(page, size);
            }
            return ResponseEntityUtils.ok(articles);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户的文章列表
     */
    @GetMapping("/my/list")
    public ResponseEntity<List<Article>> getMyArticles() {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            List<Article> articles = articleService.findByAuthorId(loginUser.getUserId());
            return ResponseEntityUtils.ok(articles);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 删除文章（同时删除该文章的所有图片和评论）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
        try {
            // 先查询文章信息，用于获取 authorId 和其他信息
            Article article = articleService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("文章不存在"));

            // 删除该文章的所有图片
            try {
                imageService.deleteImagesByArticleId(id);
            } catch (Exception e) {
                log.warn("删除文章 {} 的图片时失败：{}", id, e.getMessage());
            }

            // 删除该文章的所有评论
            try {
                commentService.deleteCommentsByArticleId(id);
            } catch (Exception e) {
                log.warn("删除文章 {} 的评论时失败：{}", id, e.getMessage());
            }

            // 删除文章
            articleService.deleteById(id);

            // 发送消息到 RocketMQ，同步到 Elasticsearch
            articleSyncProducer.sendArticleDeleteMessage(id, article.getAuthorId());

            return ResponseEntityUtils.ok();
        } catch (Exception e) {
            log.error("删除文章失败：{}", id, e);
            return ResponseEntityUtils.fail("删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable String id, @RequestBody ArticlePublishRequest request) {
        try {
            // 参数校验
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ResponseEntityUtils.fail("文章标题不能为空");
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntityUtils.fail("文章内容不能为空");
            }
            if (request.getCategoryId() == null || request.getCategoryId().trim().isEmpty()) {
                return ResponseEntityUtils.fail("分类 ID 不能为空");
            }

            // 查询原文章
            Article originalArticle = articleService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("文章不存在"));

            // 更新文章信息
            originalArticle.setTitle(request.getTitle());
            originalArticle.setContent(request.getContent());
            originalArticle.setCategoryId(request.getCategoryId());
            originalArticle.setCategoryName(request.getCategoryName());
            originalArticle.setTags(request.getTags());

            Article updated = articleService.save(originalArticle);

            // 发送消息到 RocketMQ，同步到 Elasticsearch
            ArticleSyncMessage syncMessage = convertToSyncMessage(updated);
            articleSyncProducer.sendArticleUpdateMessage(syncMessage);

            return ResponseEntityUtils.ok(updated);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("更新失败：" + e.getMessage());
        }
    }

    /**
     * 搜索文章（基于 Elasticsearch）
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ArticleDocument>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntityUtils.fail("搜索关键词不能为空");
            }

            Page<ArticleDocument> result = articleEsService.searchArticles(keyword, page, size);
            return ResponseEntityUtils.ok(result);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("搜索失败：" + e.getMessage());
        }
    }

    /**
     * 获取统计数据（文章总数、总浏览量、总评论数）
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getStatistics() {
        try {
            // 获取文章总数
            long articleTotal = articleRepository.count();

            // 获取总浏览量和总评论数（使用聚合查询优化）
            long viewTotal = articleService.getTotalViewCount();
            long commentTotal = articleService.getTotalCommentCount();

            // 返回统计数据
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("articleTotal", articleTotal);
            result.put("viewTotal", viewTotal);
            result.put("commentTotal", commentTotal);

            return ResponseEntityUtils.ok(result);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取分类统计
     */
    @GetMapping("/categories/stats")
    public ResponseEntity<List<CategoryStats>> getCategoryStatistics() {
        try {
            List<CategoryStats> stats = articleService.getCategoryStatistics();
            return ResponseEntityUtils.ok(stats);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("获取分类统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/tags/hot")
    public ResponseEntity<List<TagCount>> getHotTags(
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<TagCount> tags = articleService.getHotTags(limit);
            return ResponseEntityUtils.ok(tags);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("获取热门标签失败：" + e.getMessage());
        }
    }

    /**
     * 将 Article 转换为 ArticleSyncMessage
     */
    private ArticleSyncMessage convertToSyncMessage(Article article) {
        ArticleSyncMessage message = new ArticleSyncMessage();
        message.setId(article.getId());
        message.setTitle(article.getTitle());
        message.setContent(article.getContent());
        message.setTags(article.getTags());
        message.setCategoryName(article.getCategoryName());
        message.setAuthorId(article.getAuthorId());
        message.setAuthorName(article.getAuthorName());
        message.setViewCount(article.getViewCount());
        message.setLikeCount(article.getLikeCount());
        message.setCreateTime(article.getCreateTime());
        message.setUpdateTime(article.getUpdateTime());
        return message;
    }
}
