package com.xcz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.security.extend.LoginUser;
import com.xcz.mapper.UserMapper;
import com.xcz.proto.entity.User;
import com.xcz.proto.enums.UserExceptionEnum;
import com.xcz.proto.enums.UserRoleEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 用户详情服务实现
 * 实现UserDetailsService接口，提供基于Spring Security的用户信息加载机制
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    /**
     * 根据用户名加载用户信息
     *
     * @param username 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 临时返回一个默认用户进行演示
        // 在真实环境中，应该从数据库获取用户信息
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        User selectOne = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUserUsername, username)
        );

        if(selectOne == null){
            log.info("{}用户不存在", username);
            UserExceptionEnum.exception(UserExceptionEnum.USER_NOT_EXIST);
        }
        if(selectOne.getUserType() == null || !selectOne.getUserType()){
            log.error("{}:用户被禁用",username);
            UserExceptionEnum.exception(UserExceptionEnum.USER_DISABLED);
        }

        return new LoginUser(selectOne.getUserId(),username, selectOne.getUserName(),selectOne.getUserPassword(), selectOne.getUserType() != null && selectOne.getUserType(),
                true, true, true, Set.of(UserRoleEnum.getByRole(selectOne.getUserRole()).name()));
    }
}
