package com.xcz.proto.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User implements Serializable {

    /*** 用户id ***/
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    /*** 用户账号 ***/
    private String userUsername;
    /*** 用户名 ***/
    private String userName;
    /*** 用户密码 ***/
    private String userPassword;
    /*** 用户角色 ***/
    private Integer userRole;
    /*** 用户最后登录时间 ***/
    private LocalDateTime userLoginTime;
    /*** 用户最后登录ip ***/
    private String userIp;
    /*** 账号状态 ***/
    private Boolean userType;
}
