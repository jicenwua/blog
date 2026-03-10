# Xcz Blog Article Service - 文章服务

<div align="center">

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring-boot)
![MyBatis Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.9-orange)
![MongoDB](https://img.shields.io/badge/MongoDB-5.2.1-green?logo=mongodb)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.11.1-yellow?logo=elasticsearch)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

## 📖 项目简介

Xcz Blog Article Service 是博客系统的文章管理微服务，负责文章的发布、编辑、删除、查询、评论、统计等核心功能。采用 Spring Boot + MyBatis Plus + MongoDB + Elasticsearch 技术栈，支持全文检索、高并发访问和分布式部署。

### ✨ 主要特性

- 📝 **文章管理** - 发布、编辑、删除、草稿
- 🏷️ **分类标签** - 文章分类、标签管理
- 💬 **评论系统** - 文章评论、回复、管理
- 📊 **数据统计** - 浏览量、点赞数、评论数统计
- 🔍 **全文搜索** - Elasticsearch 高效检索
- ❤️ **点赞收藏** - 文章互动功能
- 📈 **热门排行** - 基于浏览量的文章排行
- 🎯 **Markdown** - 支持 Markdown 格式解析

## 🏗️ 项目结构

```
xcz-blog-article/
├── src/main/java/com/xcz/article/
│   ├── XczBlogArticleApplication.java  # 启动类
│   └── article/
│       ├── controller/                   # 控制器层
│       │   ├── ArticleController.java   # 文章接口
│       │   ├── CommentController.java   # 评论接口
│       │   └── StatisticsController.java # 统计接口
│       ├── mapper/                       # 数据访问层
│       │   ├── ArticleMapper.java       # 文章 Mapper
│       │   ├── CommentMapper.java       # 评论 Mapper
│       │   └── ArticleStatisticsMapper.java # 统计 Mapper
│       ├── model/                        # 数据模型
│       │   ├── entity/                   # 实体类
│       │   │   ├── Article.java         # 文章实体
│       │   │   ├── Comment.java         # 评论实体
│       │   │   └── ArticleStatistics.java # 统计实体
│       │   └── dto/                      # 数据传输对象
│       │       ├── ArticlePublishDTO.java # 发布 DTO
│       │       └── CommentDTO.java      # 评论 DTO
│       ├── service/                      # 业务逻辑层
│       │   ├── ArticleService.java      # 文章服务接口
│       │   ├── ArticleServiceImpl.java  # 文章服务实现
│       │   ├── CommentService.java      # 评论服务接口
│       │   └── CommentServiceImpl.java  # 评论服务实现
│       ├── vo/                           # 视图对象
│       │   ├── ArticleVO.java           # 文章 VO
│       │   ├── ArticleDetailVO.java     # 文章详情 VO
│       │   └── CommentVO.java           # 评论 VO
│       └── search/                       # 搜索相关
│           ├── ElasticsearchService.java # ES 服务
│           └── Document.java            # ES 文档
│
├── src/main/resources/
│   ├── application.yml                  # 应用配置
│   └── mapper/                            # MyBatis XML
│       ├── ArticleMapper.xml            # 文章 Mapper XML
│       └── CommentMapper.xml            # 评论 Mapper XML
│
└── pom.xml                              # Maven 配置
```

## 🛠️ 技术栈

| 技术 | 版本 | 描述 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MySQL Connector | 8.0.31 | MySQL 驱动 |
| MongoDB Driver | 5.2.1 | MongoDB 驱动 |
| Elasticsearch | 8.11.1 | 全文搜索引擎 |
| Redisson | 3.50.0 | Redis 客户端 |
| Lombok | 1.18.30 | 代码简化 |
| FastJSON2 | 2.0.52 | JSON 解析 |

## 🚀 快速开始

### 环境要求

- JDK: 21+
- Maven: 3.6+
- MySQL: 8.0+
- MongoDB: 6.0+
- Redis: 7.0+
- Elasticsearch: 8.x
- Nacos: 2.2+

### 编译打包

```bash
cd xcz-blog-article

# 清理编译
mvn clean

# 编译打包
mvn package -DskipTests
```

### 启动服务

```bash
java-jar target/xcz-blog-article-dev-1.0-SNAPSHOT.jar
```

服务端口：`8082`

## 📋 API 接口

### 1. 获取文章列表

**接口**：`GET /article/list`

**请求参数**：
```
page=1&size=10&tag=Java&category=技术
```

**响应结果**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "articleId": 123456789,
        "title": "Spring Boot 入门教程",
        "summary": "本文介绍 Spring Boot 的基本概念和使用方法...",
        "author": "张三",
        "viewCount": 1024,
        "likeCount": 56,
        "commentCount": 12,
        "tags": ["Java", "Spring Boot"],
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 2. 获取文章详情

**接口**：`GET /article/detail/{id}`

**响应结果**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "articleId": 123456789,
    "title": "Spring Boot 入门教程",
    "content": "# Spring Boot 入门\n\n## 什么是 Spring Boot\n\nSpring Boot...",
    "summary": "本文介绍 Spring Boot 的基本概念和使用方法...",
    "author": "张三",
    "authorId": 987654321,
    "viewCount": 1025,
    "likeCount": 56,
    "commentCount": 12,
    "tags": ["Java", "Spring Boot"],
    "category": "技术",
    "status": "PUBLISHED",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-02T15:30:00"
  }
}
```

### 3. 发布文章

**接口**：`POST /article/publish`

**请求头**：
```
Authorization: Bearer {token}
```

**请求参数**：
```json
{
  "title": "Spring Boot 入门教程",
  "content": "# Spring Boot 入门\n\n## 什么是 Spring Boot\n\nSpring Boot...",
  "summary": "本文介绍 Spring Boot 的基本概念和使用方法...",
  "tags": ["Java", "Spring Boot"],
  "category": "技术",
  "status": "PUBLISHED"
}
```

**响应结果**：
```json
{
  "code": 200,
  "message": "发布成功",
  "data": 123456789
}
```

### 4. 更新文章

**接口**：`PUT /article/update/{id}`

**请求参数**：
```json
{
  "title": "Spring Boot 入门教程（更新版）",
  "content": "# Spring Boot 入门\n\n## 什么是 Spring Boot\n\n更新后的内容...",
  "tags": ["Java", "Spring Boot", "Tutorial"],
  "category": "技术"
}
```

### 5. 删除文章

**接口**：`DELETE /article/delete/{id}`

**响应结果**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 6. 获取我的文章

**接口**：`GET /article/my/list`

**请求参数**：
```
page=1&size=10&status=PUBLISHED
```

### 7. 文章搜索

**接口**：`GET /article/search`

**请求参数**：
```
keyword=Spring Boot&page=1&size=10
```

**响应结果**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "articleId": 123456789,
        "title": "<em>Spring Boot</em>入门教程",
        "summary": "本文介绍<em>Spring Boot</em>的基本概念...",
        "highlight": "这是一篇关于<strong>Spring Boot</strong>的入门教程..."
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

### 8. 添加评论

**接口**：`POST/article/comment`

**请求参数**：
```json
{
  "articleId": 123456789,
  "content": "写得很好，学习了！",
  "parentId": null
}
```

### 9. 获取评论列表

**接口**：`GET /article/comments/{articleId}`

**响应结果**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "commentId": 111,
      "userId": 987654321,
      "username": "李四",
      "content": "写得很好，学习了！",
      "likeCount": 5,
      "createTime": "2024-01-02T10:00:00",
      "replies": [
        {
          "commentId": 112,
          "userId": 123456789,
          "username": "张三",
          "content": "感谢支持！",
          "createTime": "2024-01-02T11:00:00"
        }
      ]
    }
  ]
}
```

### 10. 获取统计数据

**接口**：`GET /article/statistics`

**响应结果**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalArticles": 100,
    "totalViews": 50000,
    "totalLikes": 2000,
    "totalComments": 500,
    "todayViews": 1024,
    "weekViews": 5000,
    "monthViews": 20000
  }
}
```

## 💾 数据库设计

### 文章表 (t_article)

```sql
CREATE TABLE `t_article` (
  `article_id` bigint NOT NULL COMMENT '文章 ID',
  `user_id` bigint NOT NULL COMMENT '作者 ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '摘要',
  `content` text COMMENT '内容 (Markdown)',
  `content_html` longtext COMMENT '内容 (HTML)',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `tags` varchar(200) DEFAULT NULL COMMENT '标签 (JSON 数组)',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论数',
  `favorite_count` int NOT NULL DEFAULT 0 COMMENT '收藏数',
  `status` varchar(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态 (PUBLISHED/DRAFT)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';
```

### 评论表 (t_comment)

```sql
CREATE TABLE `t_comment` (
  `comment_id` bigint NOT NULL COMMENT '评论 ID',
  `article_id` bigint NOT NULL COMMENT '文章 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论 ID',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` varchar(20) NOT NULL DEFAULT 'VISIBLE' COMMENT '状态 (VISIBLE/HIDDEN)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`comment_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
```

### 文章统计表 (t_article_statistics)

```sql
CREATE TABLE `t_article_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `article_id` bigint NOT NULL COMMENT '文章 ID',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论数',
  `favorite_count` int NOT NULL DEFAULT 0 COMMENT '收藏数',
  `daily_views` json DEFAULT NULL COMMENT '每日浏览量 (JSON)',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章统计表';
```

### MongoDB 文档

存储文章详情和富文本内容：

```javascript
// collection: article_detail
{
  "_id": ObjectId("..."),
  "articleId": 123456789,
  "content": "# Spring Boot 入门\n\n## 什么是 Spring Boot\n\nSpring Boot...",
  "contentHtml": "<h1>Spring Boot 入门</h1><h2>什么是 Spring Boot</h2><p>Spring Boot...</p>",
  "images": [
    "http://example.com/image1.png",
    "http://example.com/image2.png"
  ],
  "videos": [
    "http://example.com/video1.mp4"
  ],
  "attachments": [
    {
      "name": "示例代码.zip",
      "url": "http://example.com/code.zip",
      "size": 1024000
    }
  ]
}
```

### Elasticsearch 索引

用于全文检索：

```javascript
// index: article
{
  "articleId": 123456789,
  "title": "Spring Boot 入门教程",
  "summary": "本文介绍 Spring Boot 的基本概念和使用方法...",
  "content": "Spring Boot is a framework for building applications...",
  "tags": ["Java", "Spring Boot"],
  "category": "技术",
  "author": "张三",
  "createTime": "2024-01-01T10:00:00"
}
```

## 🔧 核心代码

### Controller 示例

```java
@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleController {
    
    @Resource
  private ArticleService articleService;
    
    /**
     * 获取文章列表
     */
    @GetMapping("/list")
   public Result<IPage<ArticleVO>> getArticleList(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(required = false) String tag,
        @RequestParam(required = false) String category
    ) {
        IPage<ArticleVO> articleList = articleService.getArticleList(page, size, tag, category);
        return Result.ok(articleList);
    }
    
    /**
     * 获取文章详情
     */
    @GetMapping("/detail/{id}")
   public Result<ArticleDetailVO> getArticleDetail(@PathVariable Long id) {
        ArticleDetailVO detail= articleService.getArticleDetail(id);
        return Result.ok(detail);
    }
    
    /**
     * 发布文章
     */
    @PostMapping("/publish")
    @RequiresLogin
   public Result<Long> publishArticle(@RequestBody ArticlePublishDTO dto) {
        Long userId = SecurityUtils.getUserId();
        Long articleId = articleService.publishArticle(userId, dto);
        return Result.ok(articleId);
    }
    
    /**
     * 更新文章
     */
    @PutMapping("/update/{id}")
    @RequiresLogin
   public Result<Void> updateArticle(
        @PathVariable Long id,
        @RequestBody ArticlePublishDTO dto
    ) {
        articleService.updateArticle(id, dto);
        return Result.ok();
    }
    
    /**
     * 删除文章
     */
    @DeleteMapping("/delete/{id}")
    @RequiresLogin
   public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.ok();
    }
}
```

### Service 示例

```java
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    
    @Resource
  private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
  private ElasticsearchService elasticsearchService;
    
    @Override
    @Cacheable(value = "article", key = "#id", unless = "#result == null")
   public ArticleDetailVO getArticleDetail(Long id) {
        // 查询文章
        Article article = getById(id);
      if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 从 MongoDB 获取详细内容
        ArticleDetailMongo mongo = articleDetailRepository.findByArticleId(id);
        
        // 构建 VO
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtil.copyProperties(article, vo);
      if (mongo != null) {
            vo.setContent(mongo.getContent());
            vo.setContentHtml(mongo.getContentHtml());
        }
        
        // 异步增加浏览量
        incrementViewCount(id);
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
   public Long publishArticle(Long userId, ArticlePublishDTO dto) {
        // 生成文章 ID
        Long articleId = IdGenerateManager.nextId();
        
        // 创建文章
        Article article = new Article();
        article.setArticleId(articleId);
        article.setUserId(userId);
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setCategory(dto.getCategory());
        article.setTags(JSON.toJSONString(dto.getTags()));
        article.setStatus(dto.getStatus());
        
        save(article);
        
        // 保存详细内容到 MongoDB
        ArticleDetailMongo mongo = new ArticleDetailMongo();
        mongo.setArticleId(articleId);
        mongo.setContent(dto.getContent());
        mongo.setContentHtml(MarkdownToHtml(dto.getContent()));
        articleDetailRepository.save(mongo);
        
        // 同步到 Elasticsearch
        elasticsearchService.indexArticle(article);
        
        log.info("文章发布成功 - articleId: {}", articleId);
        
        return articleId;
    }
    
    /**
     * 异步增加浏览量
     */
    @Async
   public void incrementViewCount(Long articleId) {
        String key = "article:view:" + articleId;
        redisTemplate.opsForValue().increment(key);
        
        // 异步更新数据库
        lambdaUpdate()
            .eq(Article::getArticleId, articleId)
            .setSql("view_count = view_count + 1")
            .update();
    }
}
```

## 🔍 Elasticsearch 集成

### 索引映射

```java
@Document(indexName = "article")
@Data
public class ArticleDocument {
    
    @Id
   private String articleId;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
   private String title;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
   private String content;
    
    @Field(type = FieldType.Keyword)
   private String category;
    
    @Field(type = FieldType.Keyword)
   private List<String> tags;
    
    @Field(type = FieldType.Long)
   private Long userId;
    
    @Field(type = FieldType.Date)
   private Date createTime;
}
```

### 搜索服务

```java
@Service
@Slf4j
public class ElasticsearchService {
    
    @Resource
  private ElasticsearchRestTemplate elasticsearchTemplate;
    
    /**
     * 搜索文章
     */
   public IPage<ArticleVO> search(String keyword, Integer page, Integer size) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content", "summary"))
            .withPageable(PageRequest.of(page - 1, size))
            .withHighlightFields(
                new HighlightFieldBuilder("title").preTags("<em>").postTags("</em>"),
                new HighlightFieldBuilder("content").preTags("<em>").postTags("</em>")
            )
            .build();
        
        SearchHits<ArticleDocument> searchHits = elasticsearchTemplate.search(query, ArticleDocument.class);
        
        // 处理高亮结果
        List<ArticleVO> articles = searchHits.getSearchHits().stream()
            .map(hit -> {
                ArticleVO vo = hit.getContent();
                Map<String, List<String>> highlights = hit.getHighlightFields();
                
              if (highlights.containsKey("title")) {
                    vo.setTitle(highlights.get("title").get(0));
                }
                
                return vo;
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(articles, PageRequest.of(page - 1, size), searchHits.getTotalHits());
    }
    
    /**
     * 索引文章
     */
   public void indexArticle(Article article) {
        ArticleDocument document = new ArticleDocument();
        document.setArticleId(article.getArticleId().toString());
        document.setTitle(article.getTitle());
        document.setContent(article.getContent());
        document.setCategory(article.getCategory());
        document.setTags(JSON.parseArray(article.getTags(), String.class));
        document.setUserId(article.getUserId());
        document.setCreateTime(new Date());
        
        elasticsearchTemplate.save(document);
    }
    
    /**
     * 删除文章索引
     */
   public void deleteArticle(Long articleId) {
        elasticsearchTemplate.delete(String.valueOf(articleId), ArticleDocument.class);
    }
}
```

## 📊 缓存策略

### Redis 缓存

```java
// 缓存文章详情
@Cacheable(value = "article", key = "#id", unless = "#result == null")
public ArticleDetailVO getArticleDetail(Long id) {
    // ...
}

// 清除文章缓存
@CacheEvict(value = "article", key = "#id")
public void updateArticle(Long id, ArticlePublishDTO dto) {
    // ...
}

// 缓存文章浏览量
public Long getViewCount(Long articleId) {
    String key = "article:view:" + articleId;
    return (Long) redisTemplate.opsForValue().get(key);
}
```

## 🐛 常见问题

### 1. 文章内容丢失

检查 MongoDB 连接和文档是否正确保存。

### 2. 搜索不生效

确认 Elasticsearch 索引已正确创建并同步数据。

### 3. 浏览量统计不准确

浏览量采用异步更新，可能存在短暂延迟。

## 📝 API 文档

Swagger 文档地址：
```
http://localhost:8082/doc.html
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

MIT License

---

<div align="center">

**喜欢这个项目吗？请给一个 ⭐️ Star 支持！**

</div>
