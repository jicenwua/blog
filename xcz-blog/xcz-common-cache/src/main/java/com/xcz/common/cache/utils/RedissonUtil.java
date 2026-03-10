package com.xcz.common.cache.utils;

import com.xcz.common.cache.extend.RedissonClientEnum;
import com.xcz.common.core.utils.SpringUtils;
import org.redisson.api.RedissonClient;

/**
 * redis 工具
 */
public class RedissonUtil {

    /**
     * 获取需要的redis连接数据库
     * @param redissonClientEnum    数据库枚举
     * @return  redissonClient
     */
    public static RedissonClient getRedissonClient(RedissonClientEnum redissonClientEnum) {
        return SpringUtils.getBean(redissonClientEnum.getDataBaseBeanName());
    }

}
