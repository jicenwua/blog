package com.xcz.article.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 图片实体类
 * 用于存储文章中的图片
 */
@Data
@Document(collection = "image")
public class Image {

    /**
     * 主键 ID (MongoDB 的 ObjectId)
     */
    @Id
    private String id;

    /**
     * 图片名称 (原始文件名)
     */
    @Field("file_name")
    private String fileName;

    /**
     * 图片存储路径 (MongoDB 中存储为 Base64 或 GridFS ID)
     */
    @Field("storage_path")
    private String storagePath;

    /**
     * 图片 URL (访问地址)
     */
    @Field("image_url")
    private String imageUrl;

    /**
     * 图片大小 (字节)
     */
    @Field("file_size")
    private Long fileSize;

    /**
     * 图片类型 (MIME 类型)
     */
    @Field("content_type")
    private String contentType;

    /**
     * 关联的文章 ID
     */
    @Field("article_id")
    private String articleId;

    /**
     * 上传的用户 ID
     */
    @Field("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @CreatedDate
    @Field("create_time")
    private LocalDateTime createTime;
}
