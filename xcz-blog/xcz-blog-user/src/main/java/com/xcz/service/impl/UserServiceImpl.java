package com.xcz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xcz.common.core.constant.Constants;
import com.xcz.common.core.domain.ResponseEntity;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.security.extend.LoginUser;
import com.xcz.mapper.UserMapper;
import com.xcz.proto.entity.User;
import com.xcz.proto.entity.UserSelect;
import com.xcz.proto.enums.UserExceptionEnum;
import com.xcz.proto.enums.UserRoleEnum;
import com.xcz.service.UserService;
import com.xcz.tool.id_gener.util.IdGenerateManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(User user) {
        String username = user.getUserUsername();
        String userPassword = user.getUserPassword();
        if(userPassword == null){
            log.error("{}:注册输入密码为空",username);
            UserExceptionEnum.exception(UserExceptionEnum.PASSWORD_IS_NULL);
        }
        if(userPassword.length() < 6){
            log.error("{}:注册输入密码长度小于6",username);
            UserExceptionEnum.exception(UserExceptionEnum.PASSWORD_LENGTH_ERROR);
        }

        User sameUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUserUsername, username)
        );
        if(sameUser != null){
            log.error("{}:账号已注册",username);
            UserExceptionEnum.exception(UserExceptionEnum.USER_EXIST);
        }

        user.setUserPassword(passwordEncoder.encode(userPassword));
        user.setUserRole(UserRoleEnum.USER.getRole());
        user.setUserLoginTime(LocalDateTime.now());
        user.setUserType(true);
        user.setUserId(IdGenerateManager.USER.getId());
        int insert = userMapper.insert(user);
        if(insert <= 0){
            log.error("{}:系统注册玩家失败",user);
            UserExceptionEnum.exception(UserExceptionEnum.REGISTER_FAIL);
        }

        return user ;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean login(LoginUser loginUser) {
        int update = userMapper.update(
                new LambdaUpdateWrapper<User>()
                        .eq(User::getUserId, loginUser.getUserId())
                        .set(User::getUserLoginTime, LocalDateTime.now())
                        .set(User::getUserIp, loginUser.getIpaddr())
        );
        if(update <= 0){
            log.error("{}:更新玩家登录信息失败",loginUser.getUsername());
            UserExceptionEnum.exception(UserExceptionEnum.UPDATE_FAIL);
        }

        return true;
    }

    @Override
    public ResponseEntity<List<User>> info(UserSelect userSelect) {
        // 处理分页参数，默认为第一页，每页 10 条
        int pageNum = userSelect.getPageNum() > 0 ? userSelect.getPageNum() : 1;
        int pageSize = userSelect.getPageSize() > 0 ? userSelect.getPageSize() : 10;

        Page<User> page = new Page<>(pageNum, pageSize);

        // 构建查询条件，处理空值情况
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 如果用户名不为空且不为空字符串，则添加模糊查询条件
        if (StringUtils.isNotEmpty(userSelect.getUsername())) {
            wrapper.like(User::getUserUsername, userSelect.getUsername());
        }

        // 如果用户名称不为空且不为空字符串，则添加模糊查询条件
        if (StringUtils.isNotEmpty(userSelect.getName())) {
            wrapper.like(User::getUserName, userSelect.getName());
        }

        // 执行分页查询
        Page<User> userPage = userMapper.selectPage(page, wrapper);

        // 构建响应对象
        ResponseEntity<List<User>> response = new ResponseEntity<>();
        response.setRows(userPage.getRecords());
        response.setTotal(userPage.getTotal());
        response.setCode(Constants.SUCCESS);
        return response;
    }

    public boolean update(User user) {
        if(user.getUserPassword() != null){
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }

        // 使用 LambdaUpdateWrapper 进行选择性更新，只更新非 null 字段
        int update = userMapper.update(
                new LambdaUpdateWrapper<User>()
                        .eq(User::getUserId, user.getUserId())
                        .set(user.getUserName() != null, User::getUserName, user.getUserName())
                        .set(user.getUserRole() != null, User::getUserRole, user.getUserRole())
                        .set(user.getUserPassword() != null, User::getUserPassword, user.getUserPassword())
                        .set(user.getUserType() != null, User::getUserType, user.getUserType())
        );
        return update > 0;
    }
}
