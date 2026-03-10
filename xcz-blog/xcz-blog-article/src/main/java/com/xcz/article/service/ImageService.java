package com.xcz.article.service;

import com.xcz.article.dto.ImageUploadResponse;
import com.xcz.article.entity.Image;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片服务接口
 */
public interface ImageService {

    /**
     * 上传图片到 MongoDB
     * @param file 图片文件
     * @param userId 用户 ID
     * @param articleId 文章 ID (可选)
     * @return 上传响应
     */
    ImageUploadResponse uploadImage(MultipartFile file, Long userId, String articleId);

    /**
     * 根据 ID 获取图片
     * @param imageId 图片 ID
     * @return 图片字节数组
     */
    Image getImageById(String imageId);


    /**
     * 删除文章的所有图片
     * @param articleId 文章 ID
     */
    void deleteImagesByArticleId(String articleId);
}
