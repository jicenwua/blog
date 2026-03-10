package com.xcz.article.mq.consumer;

import com.xcz.article.document.ArticleDocument;
import com.xcz.article.dto.ArticleSyncMessage;
import com.xcz.article.repository.es.ArticleEsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 文章同步消息消费者
 *
 * @author xcz
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${rocketmq.consumer.article-sync-topic:article-sync-topic}",
    consumerGroup = "article-es-sync-consumer-group",
    selectorExpression = "${rocketmq.consumer.article-sync-tag:sync}"
)
@RequiredArgsConstructor
public class ArticleSyncConsumer implements RocketMQListener<ArticleSyncMessage> {

    private final ArticleEsRepository articleEsRepository;

    @Override
    public void onMessage(ArticleSyncMessage message) {
        try {
            ArticleSyncMessage.OperationType operationType = message.getOperationType();

            switch (operationType) {
                case CREATE:
                    handleCreate(message);
                    break;
                case UPDATE:
                    handleUpdate(message);
                    break;
                case DELETE:
                    handleDelete(message);
                    break;
                default:
                    log.warn("未知的操作类型：{}", operationType);
            }
        } catch (Exception e) {
            log.error("处理文章同步消息失败：{}", message, e);
            throw new RuntimeException("处理失败", e);
        }
    }

    /**
     * 处理文章创建
     */
    private void handleCreate(ArticleSyncMessage message) {
        ArticleDocument document = convertToDocument(message);
        articleEsRepository.save(document);
    }

    /**
     * 处理文章更新
     */
    private void handleUpdate(ArticleSyncMessage message) {
        // 先检查是否存在
        Optional<ArticleDocument> existing = articleEsRepository.findById(message.getId());
        if (existing.isPresent()) {
            ArticleDocument document = convertToDocument(message);
            articleEsRepository.save(document);
        } else {
            log.warn("文章不存在，跳过更新：id={}", message.getId());
        }
    }

    /**
     * 处理文章删除
     */
    private void handleDelete(ArticleSyncMessage message) {
        articleEsRepository.deleteById(message.getId());
    }

    /**
     * 将消息转换为 Elasticsearch 文档
     */
    private ArticleDocument convertToDocument(ArticleSyncMessage message) {
        ArticleDocument document = new ArticleDocument();
        document.setId(message.getId());
        document.setTitle(message.getTitle());
        document.setContent(message.getContent());
        document.setTags(message.getTags());
        document.setCategoryName(message.getCategoryName());
        document.setAuthorId(message.getAuthorId());
        document.setAuthorName(message.getAuthorName());
        document.setViewCount(message.getViewCount());
        document.setLikeCount(message.getLikeCount());
        document.setCreateTime(message.getCreateTime());
        document.setUpdateTime(message.getUpdateTime());
        return document;
    }
}
