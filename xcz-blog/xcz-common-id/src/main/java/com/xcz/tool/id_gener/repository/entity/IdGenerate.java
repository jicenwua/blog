package com.xcz.tool.id_gener.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdGenerate {
    /***主键**/
    @TableId
    Long id;
    /***初始id**/
    Long initId;
    /***当前最大id**/
    Long maxId;
    /***步长**/
    Long step;
    /***结束id**/
    Long endId;
    /***tag**/
    String tag;
    /***id描述**/
    String description;
    /***更新时间**/
    LocalDateTime updateTime;

}
