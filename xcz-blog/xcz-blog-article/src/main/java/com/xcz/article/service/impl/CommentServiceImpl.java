package com.xcz.article.service.impl;

import com.xcz.article.dto.CommentRequest;
import com.xcz.article.dto.CommentResponse;
import com.xcz.article.entity.Article;
import com.xcz.article.entity.Comment;
import com.xcz.article.repository.ArticleRepository;
import com.xcz.article.repository.CommentRepository;
import com.xcz.article.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 *
 * @author xcz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * 评论类型常量
     */
    private static final Integer COMMENT_TYPE_ARTICLE = 1; // 文章评论
    private static final Integer COMMENT_TYPE_FLOOR = 2;   // 楼层评论

    @Override
    public CommentResponse addComment(CommentRequest request, Long userId, String userNickname, String userAvatar) {
        // 参数校验
        if (request.getArticleId() == null || request.getArticleId().trim().isEmpty()) {
            throw new IllegalArgumentException("文章 ID 不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }

        // 检查文章是否存在
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));

        // 创建评论对象
        Comment comment = new Comment();
        comment.setCommentType(request.getParentCommentId() != null ? COMMENT_TYPE_FLOOR : COMMENT_TYPE_ARTICLE);
        comment.setArticleId(request.getArticleId());
        comment.setParentCommentId(request.getParentCommentId());
        comment.setUserId(userId);
        comment.setUserNickname(userNickname);
        comment.setUserAvatar(userAvatar);
        comment.setContent(request.getContent());
        comment.setLikeCount(0L);
        comment.setReplyCount(0L);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        // 保存评论
        Comment saved = commentRepository.save(comment);

        // 如果是回复评论，增加父评论的回复数
        if (request.getParentCommentId() != null) {
            Query query = new Query(Criteria.where("_id").is(request.getParentCommentId()));
            Update update = new Update().inc("reply_count", 1);
            mongoTemplate.updateFirst(query, update, Comment.class);
        } else {
            // 如果是一级评论，增加文章的评论数
            Query query = new Query(Criteria.where("_id").is(request.getArticleId()));
            Update update = new Update().inc("comment_count", 1);
            mongoTemplate.updateFirst(query, update, Article.class);
        }

        return convertToResponse(saved);
    }

    @Override
    public Page<CommentResponse> getArticleComments(String articleId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByArticleIdAndCommentType(
                articleId, COMMENT_TYPE_ARTICLE, pageable);

        // 转换为响应对象，并加载回复列表
        List<CommentResponse> responses = commentPage.getContent().stream()
                .map(this::convertToResponseWithReplies)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(responses, pageable, commentPage.getTotalElements());
    }

    @Override
    public List<CommentResponse> getCommentReplies(String parentCommentId) {
        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreateTimeAsc(parentCommentId);
        return replies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void likeComment(String commentId) {
        Query query = new Query(Criteria.where("_id").is(commentId));
        Update update = new Update().inc("like_count", 1);
        mongoTemplate.updateFirst(query, update, Comment.class);
    }

    @Override
    public void deleteComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        // 如果是文章评论，减少文章的评论数
        if (comment.getCommentType() == COMMENT_TYPE_ARTICLE) {
            Query query = new Query(Criteria.where("_id").is(comment.getArticleId()));
            Update update = new Update().inc("comment_count", -1);
            mongoTemplate.updateFirst(query, update, Article.class);

            // 删除该评论及其所有回复
            deleteCommentAndReplies(commentId);
        } else {
            // 如果是楼层评论，减少父评论的回复数
            if (comment.getParentCommentId() != null) {
                Query query = new Query(Criteria.where("_id").is(comment.getParentCommentId()));
                Update update = new Update().inc("reply_count", -1);
                mongoTemplate.updateFirst(query, update, Comment.class);
            }
            commentRepository.deleteById(commentId);
        }
    }

    @Override
    public Long countArticleComments(String articleId) {
        return commentRepository.countByArticleIdAndCommentType(articleId, COMMENT_TYPE_ARTICLE);
    }

    @Override
    public Page<CommentResponse> getAllComments(int page, int size) {
        // 分页查询所有评论（按创建时间倒序）
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Comment> commentPage = commentRepository.findAllByOrderByCreateTimeDesc(pageable);
        
        // 转换为响应对象
        return commentPage.map(this::convertToResponse);
    }

    @Override
    public void deleteCommentsByArticleId(String articleId) {
        log.info("删除文章 {} 的所有评论", articleId);
        // 直接使用 Repository 的批量删除方法
        commentRepository.deleteByArticleId(articleId);
        log.info("已删除文章 {} 的所有评论", articleId);
    }

    /**
     * 删除评论及其所有回复
     */
    private void deleteCommentAndReplies(String commentId) {
        // 查询所有回复
        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreateTimeAsc(commentId);
        // 递归删除回复
        for (Comment reply : replies) {
            deleteCommentAndReplies(reply.getId());
        }
        // 删除当前评论
        commentRepository.deleteById(commentId);
    }

    /**
     * 转换为响应对象 (不包含回复)
     */
    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setCommentType(comment.getCommentType());
        response.setUserId(comment.getUserId());
        response.setUserNickname(comment.getUserNickname());
        response.setUserAvatar(comment.getUserAvatar());
        response.setContent(comment.getContent());
        response.setLikeCount(comment.getLikeCount());
        response.setReplyCount(comment.getReplyCount());
        response.setCreateTime(comment.getCreateTime());
        return response;
    }

    /**
     * 转换为响应对象 (包含回复列表)
     */
    private CommentResponse convertToResponseWithReplies(Comment comment) {
        CommentResponse response = convertToResponse(comment);

        // 加载回复列表
        if (comment.getReplyCount() > 0) {
            List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreateTimeAsc(comment.getId());
            List<CommentResponse> replyResponses = replies.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            response.setReplies(replyResponses);
        }

        return response;
    }
}
