package com.xcz.proto.enums;

import com.xcz.common.core.exception.base.BaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserExceptionEnum   {

    PASSWORD_IS_NULL(1000,"密码不能为空"),
    PASSWORD_LENGTH_ERROR(1001,"密码长度不能小于6位"),
    USER_EXIST(1002,"账号已存在"),
    REGISTER_FAIL(1003,"注册失败"),
    USER_PASSWORD_ERROR(1004,"账号或密码错误"),
    USER_DISABLED(1005,"账号已禁用"),
    UPDATE_FAIL(1006,"更新失败"),
    USER_NOT_EXIST(1007,"用户不存在")

    ;

    private final int code;
    private final String message;

    public static void exception(UserExceptionEnum userExceptionEnum){
        throw  new BaseException(userExceptionEnum.getCode(),userExceptionEnum.getMessage());
    }

}
