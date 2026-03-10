package com.xcz.common.database.extend;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.List;

/**
 * 自动生成实体类
 */
public class GeneraCode {
    private final DataSourceConfig dataSourceConfig;

    public GeneraCode(String url, String username, String password) {
        this.dataSourceConfig = new DataSourceConfig.Builder(url, username, password).build();
    }

    /**
     * 生成多个实体类
     *
     * @param tableNames  表名
     * @param packageName 包名
     */
    public void generateList(List<String> tableNames, String packageName) {
        tableNames.forEach(tableName -> generate(tableName, packageName));
    }


    /**
     * 生成单个实体类
     *
     * @param tableName   表名
     * @param packageName 包名
     */
    public void generate(String tableName, String packageName) {
        // 1. 数据源配置（使用 yml 注入的值）

        // 2. 全局配置
        GlobalConfig globalConfig = new GlobalConfig.Builder()
                .author("ypl")  // 作者
                .outputDir(System.getProperty("user.dir") + "/src/main/java") // 输出目录
                .disableOpenDir()     // 禁止生成后打开文件夹
                .commentDate("yyyy-MM-dd") // 注释日期格式
                .build();;

        // 3. 包配置
        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent(packageName)
                .moduleName("proto")
                .build();

        // 4. 策略配置
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .addInclude(tableName) // 指定表
                .entityBuilder()
                .enableLombok()    // 启用 Lombok
                .naming(NamingStrategy.underline_to_camel)   // 表名下划线转驼峰
                .columnNaming(NamingStrategy.underline_to_camel) // 字段下划线转驼峰
                .build()
                .controllerBuilder()
                .enableRestStyle()   // @RestController
                .enableHyphenStyle() // url 中驼峰转连字符
                .build()
                .serviceBuilder()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl")
                .build()
                .mapperBuilder()
                .enableBaseResultMap() // 生成通用 resultMap
                .enableBaseColumnList() // 生成通用 columnList
                .build();

        TemplateConfig templateConfig = new TemplateConfig.Builder()
                .entity("templates/entities.java.vm")  // 使用绝对路径，从 resources 目录开始
                .build();

        // 5. 构建生成器并执行
        AutoGenerator generator = new AutoGenerator(dataSourceConfig)
                .global(globalConfig)
                .packageInfo(packageConfig)
                .strategy(strategyConfig)
                .template(templateConfig);

        generator.execute();
    }
}
