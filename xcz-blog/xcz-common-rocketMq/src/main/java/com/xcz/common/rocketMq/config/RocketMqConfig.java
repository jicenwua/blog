package com.xcz.common.rocketMq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import java.util.List;

@Configuration
public class RocketMqConfig {

    /**
     * 解决 RocketMQ Jackson序列化 LocalDateTime 等类型报错的问题
     * 覆盖默认的 MessageConverter
     */
    @Bean
    @Primary
    public RocketMQMessageConverter createRocketMQMessageConverter() {
        RocketMQMessageConverter converter = new RocketMQMessageConverter();
        CompositeMessageConverter compositeMessageConverter = (CompositeMessageConverter) converter.getMessageConverter();
        List<MessageConverter> converters = compositeMessageConverter.getConverters();

        for (MessageConverter mc : converters) {
            if (mc instanceof MappingJackson2MessageConverter) {
                MappingJackson2MessageConverter jacksonConverter = (MappingJackson2MessageConverter) mc;
                ObjectMapper objectMapper = jacksonConverter.getObjectMapper();
                // 注册 Java 8 时间模块
                objectMapper.registerModule(new JavaTimeModule());
                // 禁用把日期转换成时间戳
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        }
        return converter;
    }
}
