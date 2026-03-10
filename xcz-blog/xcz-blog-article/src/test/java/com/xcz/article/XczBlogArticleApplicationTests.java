package com.xcz.article;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XczBlogArticleApplicationTests {

    @Test
    void contextLoads() {
    }

//    docker run -d `
//            --name mongodb-xcz `
//            -p 27017:27017 `
//            -v C:\mongo-data:/data/db `
//            -e MONGO_INITDB_ROOT_USERNAME=admin `
//            -e MONGO_INITDB_ROOT_PASSWORD=594088 `
//    mongo:latest
//    version: '3.8'
//
//    services:
//    mongodb:
//    image: mongo:latest
//    container_name: mongodb-xcz
//    restart: always
//    ports:
//            - "27017:27017"
//    volumes:
//            - ./mongo-data:/data/db
//    environment:
//            # 设置超级管理员账号
//      - MONGO_INITDB_ROOT_USERNAME=admin
//      # 设置超级管理员密码
//      - MONGO_INITDB_ROOT_PASSWORD=your_password
//      # 初始创建的数据库
//      - MONGO_INITDB_DATABASE=xcz_blog_article
//
//    networks:
//    default:
//    name: xcz-network

}
