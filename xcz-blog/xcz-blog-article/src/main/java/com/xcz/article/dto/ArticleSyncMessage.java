package com.xcz.article.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章同步消息 DTO (用于 RocketMQ)
 *
 * @author xcz
 */
@Data
public class ArticleSyncMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章 ID
     */
    private String id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 作者 ID
     */
    private Long authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 点赞次数
     */
    private Long likeCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 操作类型：CREATE(创建), UPDATE(更新), DELETE(删除)
     */
    private OperationType operationType;

    public enum OperationType {
        CREATE,
        UPDATE,
        DELETE
    }
}
