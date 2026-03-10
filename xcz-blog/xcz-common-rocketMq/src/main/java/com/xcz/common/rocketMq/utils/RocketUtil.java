package com.xcz.common.rocketMq.utils;

import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 生产者全能工具类
 */
@Component
public class RocketUtil {

    private final Logger log = LoggerFactory.getLogger(RocketUtil.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    // 自定义异步回调线程池 (可选配置)
    static {
        // 在静态块或者配置类中设置，避免每次发送都设置
        // ExecutorService executor = Executors.newFixedThreadPool(10);
        // rocketMQTemplate.setAsyncSenderExecutor(executor);
    }

    // =================================================================
    // 1. 同步发送 (Sync) - 最常用，可靠性高
    // =================================================================

    /**
     * 同步发送普通消息
     */
    public SendResult sendMsg(String topic, Object msg) {
        return rocketMQTemplate.syncSend(topic, msg);
    }

    /**
     * 同步发送延时消息 (使用延时等级)
     * level: 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    public SendResult sendDelayMsg(String topic, Object msg, int delayLevel) {
        return rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msg).build(), 3000, delayLevel);
    }

    /**
     * 同步发送定时消息 (RocketMQ 5.0+ 或特定版本支持任意时间)
     * 注意：如果是老版本 RocketMQ，这个方法可能不可用，请使用上面的 delayLevel
     */
    public SendResult sendTimingMsg(String topic, Object msg, long deliverTimestamp) {
        return rocketMQTemplate.syncSendDelayTimeMills(topic, MessageBuilder.withPayload(msg).build(), deliverTimestamp);
    }

    // =================================================================
    // 2. 异步发送 (Async) - 高吞吐，不阻塞主线程
    // =================================================================

    /**
     * 异步发送消息
     */
    public void sendAsyncMsg(String topic, Object msg) {
        rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送成功, msgId: {}", sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable e) {
                log.error("异步发送失败, topic: {}", topic, e);
            }
        });
    }

    /**
     * 异步发送，使用自己的回调处理
     *
     * @param topic
     * @param msg
     * @param sendCallback
     */
    public void sendAsyncMsg(String topic, Object msg, SendCallback sendCallback) {
        rocketMQTemplate.asyncSend(topic, msg, sendCallback);
    }

    /**
     * 延时发送消息
     */
    public void sendAsyncMsg(String topic, Object msg, SendCallback sendCallback, long delay, int delayLevel) {
        rocketMQTemplate.asyncSend(topic, MessageBuilder.withPayload(msg).build(), sendCallback, delay, delayLevel);
    }

    // =================================================================
    // 3. 单向发送 (OneWay) - 最快，不管结果，只适合日志
    // =================================================================

    public void sendOneWayMsg(String topic, Object msg) {
        rocketMQTemplate.sendOneWay(topic, msg);
    }

    // =================================================================
    // 4. 顺序发送 (Orderly) - 保证同一hashKey的消息进同一个队列
    // =================================================================

    /**
     * 同步顺序发送
     *
     * @param hashKey 参考依据，通常是 orderId、userId，相同的 key 会进入同一队列
     */
    public SendResult sendOrderlyMsg(String topic, Object msg, String hashKey) {
        return rocketMQTemplate.syncSendOrderly(topic, msg, hashKey);
    }

    /**
     * 异步顺序发送
     */
    public void sendAsyncOrderlyMsg(String topic, Object msg, String hashKey) {
        rocketMQTemplate.asyncSendOrderly(topic, msg, hashKey, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步顺序发送成功");
            }

            @Override
            public void onException(Throwable e) {
                log.error("异步顺序发送失败", e);
            }
        });
    }

    /**
     * 单向顺序发送
     */
    public void sendOneWayOrderlyMsg(String topic, Object msg, String hashKey) {
        rocketMQTemplate.sendOneWayOrderly(topic, msg, hashKey);
    }

    // =================================================================
    // 5. 请求-应答模式 (RPC) - 等待消费者返回结果
    // =================================================================

    /**
     * 发送消息并等待响应
     *
     * @param topic         主题
     * @param msg           发送的消息体
     * @param responseClass 期望返回值的类型
     * @return 消费者返回的结果
     */
    public <T> T sendAndReceive(String topic, Object msg, Class<T> responseClass) {
        return rocketMQTemplate.sendAndReceive(topic, msg, responseClass);
    }
}
