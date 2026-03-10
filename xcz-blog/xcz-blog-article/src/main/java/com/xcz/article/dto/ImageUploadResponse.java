package com.xcz.article.dto;

import lombok.Data;

/**
 * 图片上传响应
 */
@Data
public class ImageUploadResponse {

    /**
     * 图片 ID
     */
    private String imageId;

    /**
     * 图片 URL (用于前端显示)
     */
    private String url;

    /**
     * 图片名称
     */
    private String fileName;

    /**
     * 图片大小
     */
    private Long fileSize;

    /**
     * 上传状态码
     */
    private Integer code;

    /**
     * 上传消息
     */
    private String message;
}
