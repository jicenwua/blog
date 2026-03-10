package com.xcz.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类统计数据
 *
 * @author xcz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStats {
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 文章数量
     */
    private Long articleCount;
    
    /**
     * 浏览次数
     */
    private Long viewCount;
}
