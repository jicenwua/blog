package com.xcz.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签统计数据
 *
 * @author xcz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagCount {
    /**
     * 标签名称
     */
    private String name;
    
    /**
     * 使用次数
     */
    private Long count;
}
