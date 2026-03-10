package com.xcz.common.database.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.xcz.**.mapper")
public class MybatisConfig {

    /**
     * 配置 MyBatis-Plus 分页插件
     *
     * @return PaginationInnerInterceptor
     */
    //@Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 可设置单页最大记录数，-1 表示无限制
        paginationInterceptor.setMaxLimit(-1L);
        paginationInterceptor.setDbType(DbType.MYSQL);
        paginationInterceptor.setOptimizeJoin(true);
        return paginationInterceptor;
    }

    /**
     * 配置 MyBatis-Plus 乐观锁插件
     *
     * @return OptimisticLockerInnerInterceptor
     */
    //@Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 配置 MyBatis-Plus 插件集合
     *
     * @return MybatisPlusInterceptor
     */
    //@Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 插件添加顺序：乐观锁 -> 分页（分页插件建议最后）
        mybatisPlusInterceptor.addInnerInterceptor(optimisticLockerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(paginationInterceptor());
        return mybatisPlusInterceptor;
    }
}
