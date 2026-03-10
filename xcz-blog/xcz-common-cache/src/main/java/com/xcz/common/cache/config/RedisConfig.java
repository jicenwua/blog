package com.xcz.common.cache.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.xcz.common.cache.extend.RedissonClientBeanEnum;
import com.xcz.common.cache.extend.RedissonClientEnum;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.function.Supplier;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.password}")
    private String password;

    private final Supplier<String> address = () -> "redis://" + host + ":" + port;

    private RedissonClient buildRedissonClient(RedissonClientEnum redissonClientEnum) {
        Config config = new Config();
        //单机使用redis
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(address.get())
                .setDatabase(redissonClientEnum.getDataBase())
                // 连接池最大连接数（客户端最多可用连接数），默认为64
                // 注意：因为每一个RedissonClient都会有一个单独的连接池，所以这里是每一个RedissonClient的连接数，因此需要控制连接数量限制
                //.setConnectionPoolSize(12)
                // 最小空闲连接数，保持的最小连接数量（客户端最小空闲连接数），默认为24；
                // 注意：因为每一个RedissonClient都会有一个单独的连接池，所以这里是每一个RedissonClient的连接数，减少连接资源的一直占用
                .setConnectionMinimumIdleSize(12);
        if (password != null && !password.isEmpty()) {
            singleServerConfig.setPassword(password);
        }
        ObjectMapper mapper = new ObjectMapper();
        // 自动发现并注册所有模块（包括 JavaTimeModule）
        mapper.findAndRegisterModules();
        // 时间相关配置
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 处理未知属性（避免反序列化时出错）
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 处理空值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置 JSON 序列化方式
        JsonJacksonCodec jsonJacksonCodec = new JsonJacksonCodec(mapper);
        config.setCodec(jsonJacksonCodec);
        return Redisson.create(config);

    }



    /**
     * 默认使用redis 0数据库
     */
    @Primary
    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN0)
    public RedissonClient redissonClient() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_0);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN1)
    public RedissonClient redissonClient1() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_1);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN2)
    public RedissonClient redissonClient2() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_2);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN3)
    public RedissonClient redissonClient3() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_3);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN4)
    public RedissonClient redissonClient4() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_4);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN5)
    public RedissonClient redissonClient5() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_5);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN6)
    public RedissonClient redissonClient6() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_6);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN7)
    public RedissonClient redissonClient7() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_7);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN8)
    public RedissonClient redissonClient8() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_8);
    }

    @Bean(name = RedissonClientBeanEnum.REDISSONCLIENT_BEAN9)
    public RedissonClient redissonClient9() {
        return buildRedissonClient(RedissonClientEnum.DATABASE_9);
    }



}
