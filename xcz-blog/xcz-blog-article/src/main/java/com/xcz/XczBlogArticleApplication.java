package com.xcz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 博客文章服务启动类
 * 使用 MongoDB 作为数据存储，禁用关系型数据库自动配置
 */
@EnableScheduling
@SpringBootApplication
public class XczBlogArticleApplication {

    public static void main(String[] args) {
        SpringApplication.run(XczBlogArticleApplication.class, args);
    }

}
