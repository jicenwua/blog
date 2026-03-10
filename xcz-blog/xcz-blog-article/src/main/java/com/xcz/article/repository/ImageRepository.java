package com.xcz.article.repository;

import com.xcz.article.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 图片 Repository 接口
 */
@Repository
public interface ImageRepository extends MongoRepository<Image, String> {

    /**
     * 根据文章 ID 查询图片列表
     */
    List<Image> findByArticleId(String articleId);

    /**
     * 根据用户 ID 查询图片列表
     */
    List<Image> findByUserId(Long userId);

    /**
     * 删除文章的所有图片
     */
    void deleteByArticleId(String articleId);
}
