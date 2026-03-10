package com.xcz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xcz.common.core.domain.ResponseEntity;
import com.xcz.common.security.extend.LoginUser;
import com.xcz.proto.entity.User;
import com.xcz.proto.entity.UserSelect;

import java.util.List;


public interface UserService extends IService<User> {
    User register(User user) throws Exception;

    boolean login(LoginUser user);

    boolean update(User user);


   ResponseEntity<List<User>> info(UserSelect userSelect);
}
