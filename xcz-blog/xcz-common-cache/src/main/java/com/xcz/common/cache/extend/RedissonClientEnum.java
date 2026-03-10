package com.xcz.common.cache.extend;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedissonClientEnum {
    DATABASE_0(0,RedissonClientBeanEnum.REDISSONCLIENT_BEAN0),
    DATABASE_1(1,RedissonClientBeanEnum.REDISSONCLIENT_BEAN1),
    DATABASE_2(2,RedissonClientBeanEnum.REDISSONCLIENT_BEAN2),
    DATABASE_3(3,RedissonClientBeanEnum.REDISSONCLIENT_BEAN3),
    DATABASE_4(4,RedissonClientBeanEnum.REDISSONCLIENT_BEAN4),
    DATABASE_5(5,RedissonClientBeanEnum.REDISSONCLIENT_BEAN5),
    DATABASE_6(6,RedissonClientBeanEnum.REDISSONCLIENT_BEAN6),
    DATABASE_7(7,RedissonClientBeanEnum.REDISSONCLIENT_BEAN7),
    DATABASE_8(8,RedissonClientBeanEnum.REDISSONCLIENT_BEAN8),
    DATABASE_9(9,RedissonClientBeanEnum.REDISSONCLIENT_BEAN9),
    ;

    /***数据源编号**/
    private final int dataBase;
    /***数据源beanName**/
    private final String dataBaseBeanName;

}
