package com.xcz.article.controller;

import com.xcz.article.dto.ImageUploadResponse;
import com.xcz.article.entity.Image;
import com.xcz.article.service.ImageService;
import com.xcz.common.core.domain.ResponseEntity;
import com.xcz.common.core.utils.response.ResponseEntityUtils;
import com.xcz.common.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * 图片上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageService imageService;

    /**
     * 上传图片
     * @param file 图片文件
     * @return 上传响应
     */
    @PostMapping("/upload/image")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // 获取当前登录用户（如果接口在白名单中，可能获取不到用户信息）
            Long userId = null;
            try {
                userId = SecurityUtils.getLoginUser().getUserId();
            } catch (Exception e) {
                // 如果获取用户信息失败（白名单接口），使用默认值
                userId = 0L;
            }

            // 上传图片
            ImageUploadResponse response = imageService.uploadImage(file, userId, null);

            log.info("图片上传结果: code={}, message={}", response.getCode(), response.getMessage());

            if (response.getCode() == 200) {
                return ResponseEntityUtils.ok(response);
            } else {
                return ResponseEntityUtils.fail(response.getMessage());
            }
        } catch (Exception e) {
            log.error("图片上传失败", e);
            ImageUploadResponse response = new ImageUploadResponse();
            response.setCode(500);
            response.setMessage("上传失败：" + e.getMessage());
            return ResponseEntityUtils.fail(response.getMessage());
        }
    }

    /**
     * 获取图片
     * @param imageId 图片 ID
     * @return 图片二进制数据
     */
    @GetMapping("/image/{imageId}")
    public org.springframework.http.ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
        Image imageById = imageService.getImageById(imageId);

        if (imageById == null) {
            // 抛出异常，让全局异常处理器处理
            throw new RuntimeException("未找到图片");
        }
        byte[] imageBytes = Base64.getDecoder().decode(imageById.getStoragePath());

        return org.springframework.http.ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageById.getContentType())) // 设置正确的图片类型，如 image/png
                .body(imageBytes);

    }

}
