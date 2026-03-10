package com.xcz.article;

import com.xcz.article.dto.ArticlePublishRequest;
import com.xcz.article.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

/**
 * 文章发布功能测试
 *
 * @author xcz
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArticlePublishTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * 测试文章发布接口
     */
    @Test
    public void testPublishArticle() {
        // 构建发布请求
        ArticlePublishRequest request = new ArticlePublishRequest();
        request.setTitle("Spring Boot 集成 MongoDB 完整指南");
        request.setContent("<h1>Spring Boot 集成 MongoDB</h1><p>这是文章内容...</p>");
        request.setCategoryId("java_category");
        request.setCategoryName("Java 技术");
        request.setTags(Arrays.asList("Spring Boot", "MongoDB", "NoSQL"));

        // 发送 POST 请求
        ResponseEntity<Article> response = restTemplate.postForEntity(
                "/article/publish",
                request,
                Article.class
        );

        // 验证响应
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert response.getBody().getId() != null;

        System.out.println("文章发布成功，ID: " + response.getBody().getId());
    }

    /**
     * 测试查询文章接口
     */
    @Test
    public void testGetArticleById() {
        // 先发布一篇文章
        ArticlePublishRequest request = new ArticlePublishRequest();
        request.setTitle("测试文章");
        request.setContent("测试内容");
        request.setCategoryId("test");

        ResponseEntity<Article> publishResponse = restTemplate.postForEntity(
                "/article/publish",
                request,
                Article.class
        );

        String articleId = publishResponse.getBody().getId();

        // 查询文章
        ResponseEntity<Article> getResponse = restTemplate.getForEntity(
                "/article/" + articleId,
                Article.class
        );

        assert getResponse.getStatusCode() == HttpStatus.OK;
        assert getResponse.getBody() != null;
        assert getResponse.getBody().getTitle().equals("测试文章");

        System.out.println("查询成功：" + getResponse.getBody().getTitle());
    }

    /**
     * 测试增加浏览次数
     */
    @Test
    public void testIncrementViewCount() {
        String articleId = "test_article_id"; // 替换为实际的文章 ID

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/article/" + articleId + "/view",
                null,
                Void.class
        );

        assert response.getStatusCode() == HttpStatus.OK;
        System.out.println("浏览次数增加成功");
    }

    /**
     * 测试点赞功能
     */
    @Test
    public void testLikeArticle() {
        String articleId = "test_article_id"; // 替换为实际的文章 ID

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/article/" + articleId + "/like",
                null,
                Void.class
        );

        assert response.getStatusCode() == HttpStatus.OK;
        System.out.println("点赞成功");
    }
}
