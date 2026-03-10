package com.xcz.controller;

import com.xcz.common.core.domain.ResponseEntity;
import com.xcz.common.core.exception.user.UserPasswordNotMatchException;
import com.xcz.common.core.utils.StringUtils;
import com.xcz.common.core.utils.ip.IpUtils;
import com.xcz.common.core.utils.response.ResponseEntityUtils;
import com.xcz.common.security.extend.LoginUser;
import com.xcz.common.security.service.TokenService;
import com.xcz.common.security.utils.SecurityUtils;
import com.xcz.proto.entity.User;
import com.xcz.proto.entity.UserSelect;
import com.xcz.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws Exception {
        // 获取用户真实 IP
        String ip = IpUtils.getServletIp();
        user.setUserIp(ip);
        User register = userService.register(user);
        return ResponseEntityUtils.ok(register);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUser> login(@RequestBody User user) {
        String ip = IpUtils.getServletIp();

        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用 UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUserUsername(), user.getUserPassword()));
        } catch (Exception e) {
            if (e instanceof UserPasswordNotMatchException) {
                throw e;
            }
            throw new UserPasswordNotMatchException();
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        loginUser.setIpaddr(ip);
        // 生成 token
        tokenService.createToken(loginUser);
        userService.login(loginUser);
        return ResponseEntityUtils.ok(loginUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null && StringUtils.isNotEmpty(loginUser.getToken())) {
            tokenService.delLoginUser(loginUser.getToken());
            log.info("用户退出登录成功，用户名：{}", loginUser.getUsername());
        }
        return ResponseEntityUtils.ok();
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> update(@RequestBody  User user) {
        return ResponseEntityUtils.ok(userService.update(user));
    }

    @PostMapping("/info")
    public ResponseEntity<List<User>> info(@RequestBody UserSelect userSelect){
       return userService.info(userSelect);
    }
}
