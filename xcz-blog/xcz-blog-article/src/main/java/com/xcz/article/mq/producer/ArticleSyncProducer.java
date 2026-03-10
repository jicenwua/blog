package com.xcz.article.mq.producer;

import com.xcz.article.dto.ArticleSyncMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * 文章同步消息生产者
 *
 * @author xcz
 */
@Slf4j
@Component
public class ArticleSyncProducer {

    @Value("${rocketmq.producer.article-sync-topic:article-sync-topic}")
    private String topic;

    @Value("${rocketmq.producer.article-sync-tag:sync}")
    private String tag;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送文章同步消息
     */
    public void sendArticleSyncMessage(ArticleSyncMessage message) {
        try {
            rocketMQTemplate.convertAndSend(topic + ":" + tag, message);
        } catch (MessagingException e) {
            log.error("发送文章同步消息失败：{}", e.getMessage());
        }
    }

    /**
     * 发送文章创建消息
     */
    public void sendArticleCreateMessage(ArticleSyncMessage message) {
        message.setOperationType(ArticleSyncMessage.OperationType.CREATE);
        sendArticleSyncMessage(message);
    }

    /**
     * 发送文章更新消息
     */
    public void sendArticleUpdateMessage(ArticleSyncMessage message) {
        message.setOperationType(ArticleSyncMessage.OperationType.UPDATE);
        sendArticleSyncMessage(message);
    }

    /**
     * 发送文章删除消息
     */
    public void sendArticleDeleteMessage(String articleId, Long authorId) {
        ArticleSyncMessage message = new ArticleSyncMessage();
        message.setId(articleId);
        message.setAuthorId(authorId);
        message.setOperationType(ArticleSyncMessage.OperationType.DELETE);
        sendArticleSyncMessage(message);
    }
}
