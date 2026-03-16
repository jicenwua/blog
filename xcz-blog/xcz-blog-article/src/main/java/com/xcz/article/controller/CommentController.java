package com.xcz.article.controller;

import com.xcz.article.dto.CommentRequest;
import com.xcz.article.dto.CommentResponse;
import com.xcz.article.service.CommentService;
import com.xcz.common.core.domain.ResponseEntity;
import com.xcz.common.core.utils.response.ResponseEntityUtils;
import com.xcz.common.security.extend.LoginUser;
import com.xcz.common.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 *
 * @author xcz
 */
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 发表评论
     */
    @PostMapping("/add")
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            CommentResponse response = commentService.addComment(
                    request,
                    loginUser.getUserId(),
                    loginUser.getName(),
                    null // 头像暂时传 null，可以从用户服务获取
            );
            return ResponseEntityUtils.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntityUtils.fail(e.getMessage());
        } catch (Exception e) {
            return ResponseEntityUtils.fail("评论失败：" + e.getMessage());
        }
    }

    /**
     * 获取文章的评论列表 (分页)
     */
    @GetMapping("/list/{articleId}")
    public ResponseEntity<Page<CommentResponse>> getArticleComments(
            @PathVariable String articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<CommentResponse> responses = commentService.getArticleComments(articleId, page, size);
            return ResponseEntityUtils.ok(responses);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("获取评论失败：" + e.getMessage());
        }
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<List<CommentResponse>> getCommentReplies(
            @PathVariable String parentCommentId
    ) {
        try {
            List<CommentResponse> responses = commentService.getCommentReplies(parentCommentId);
            return ResponseEntityUtils.ok(responses);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("获取回复失败：" + e.getMessage());
        }
    }

    /**
     * 点赞评论（切换点赞状态）
     */
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<Void> likeComment(@PathVariable String commentId) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            
            // 检查是否已点赞
            if (commentService.hasLiked(commentId, userId)) {
                // 取消点赞
                commentService.decrementLikeCount(commentId, userId);
            } else {
                // 增加点赞
                commentService.incrementLikeCount(commentId, userId);
            }
            
            return ResponseEntityUtils.ok();
        } catch (Exception e) {
            return ResponseEntityUtils.fail("点赞失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否已点赞评论
     */
    @GetMapping("/comment/{commentId}/liked")
    public ResponseEntity<Boolean> checkCommentLiked(@PathVariable String commentId) {
        try {
            Long userId = null;
            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                userId = loginUser.getUserId();
            } catch (Exception e) {
                // 未登录
            }

            boolean liked = commentService.hasLiked(commentId, userId);
            return ResponseEntityUtils.ok(liked);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntityUtils.ok();
        } catch (IllegalArgumentException e) {
            return ResponseEntityUtils.fail(e.getMessage());
        } catch (Exception e) {
            return ResponseEntityUtils.fail("删除失败：" + e.getMessage());
        }
    }

    /**
     * 统计文章的评论数量
     */
    @GetMapping("/count/{articleId}")
    public ResponseEntity<Long> countArticleComments(@PathVariable String articleId) {
        try {
            Long count = commentService.countArticleComments(articleId);
            return ResponseEntityUtils.ok(count);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("统计失败：" + e.getMessage());
        }
    }

    /**
     * 管理后台 - 分页查询所有评论
     */
    @GetMapping("/admin/all")
    public ResponseEntity<Page<CommentResponse>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<CommentResponse> responses = commentService.getAllComments(page, size);
            return ResponseEntityUtils.ok(responses);
        } catch (Exception e) {
            return ResponseEntityUtils.fail("获取评论失败：" + e.getMessage());
        }
    }
}
