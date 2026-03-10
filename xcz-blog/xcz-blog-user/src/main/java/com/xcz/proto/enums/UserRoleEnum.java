package com.xcz.proto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoleEnum {
    ADMIN(1),
    USER(2)
    ;

    private final int role;

    public static UserRoleEnum getByRole(int role){
        for (UserRoleEnum value : UserRoleEnum.values()) {
            if(value.getRole() == role){
                return value;
            }
        }
        return null;
    }
}
