package com.xcz.article.service.impl;

import com.xcz.article.dto.ImageUploadResponse;
import com.xcz.article.entity.Image;
import com.xcz.article.repository.ImageRepository;
import com.xcz.article.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * 图片服务实现类
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    /**
     * 图片访问基础路径
     */
    @Value("${image.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 网关路由前缀
     */
    @Value("${image.gateway-route-prefix:/xcz/blog}")
    private String gatewayRoutePrefix;

    @Override
    public ImageUploadResponse uploadImage(MultipartFile file, Long userId, String articleId) {
        ImageUploadResponse response = new ImageUploadResponse();

        try {
            // 验证文件
            if (file.isEmpty()) {
                response.setCode(400);
                response.setMessage("文件不能为空");
                return response;
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.setCode(400);
                response.setMessage("只支持图片文件");
                return response;
            }

            // 验证文件大小 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.setCode(400);
                response.setMessage("文件大小不能超过 5MB");
                return response;
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID().toString() + extension;

            // 将图片转换为 Base64 存储
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 创建 Image 实体
            Image image = new Image();
            image.setFileName(fileName);
            image.setStoragePath(base64Image); // 存储 Base64
            image.setContentType(contentType);
            image.setFileSize(file.getSize());
            image.setArticleId(articleId);
            image.setUserId(userId);
            image.setCreateTime(LocalDateTime.now());

            // 保存图片
            Image saved = imageRepository.save(image);

            // 生成访问 URL（通过网关访问）
            String imageUrl = baseUrl + gatewayRoutePrefix + "/article/image/" + saved.getId();

            // 设置响应
            response.setCode(200);
            response.setMessage("上传成功");
            response.setImageId(saved.getId());
            response.setUrl(imageUrl);
            response.setFileName(fileName);
            response.setFileSize(file.getSize());

            return response;

        } catch (IOException e) {
            response.setCode(500);
            response.setMessage("上传失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public Image getImageById(String imageId) {
        Image image = imageRepository.findById(imageId)
                .orElse(null);

        if (image == null) {
            return null;
        }

        // 从 Base64 解码返回
        return image;
    }


    @Override
    public void deleteImagesByArticleId(String articleId) {
        imageRepository.deleteByArticleId(articleId);
    }
}
