package com.xcz.common.core.domain;

import com.xcz.common.core.constant.HttpStatus;
import lombok.Data;

/**
 * ResponseEntity
 */
@Data
public class ResponseEntity<T> {
    //返回码
    private int code;
    //返回信息
    private String msg;
    //单个数据
    private T data;
    //当前页数据
    private T rows;
    //总记录数
    private Long total;

    public boolean isOk() {
        return this.code == HttpStatus.SUCCESS;
    }
}
