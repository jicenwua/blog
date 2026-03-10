//package com.xcz.common.security.service;
//
//import com.xcz.common.core.exception.user.UserPasswordNotMatchException;
//import com.xcz.common.core.utils.StringUtils;
//import com.xcz.common.security.extend.LoginUser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//
///**
// * 认证服务
// */
//@Service
//public class AuthService {
//
//    @Autowired
//    private TokenService tokenService;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    /**
//     * 登录认证
//     *
//     * @param username 用户名
//     * @param password 密码
//     * @return 登录用户信息
//     */
//    public String login(String username, String password) {
//        // 用户验证
//        Authentication authentication = null;
//        try {
//            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
//            authentication = authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (Exception e) {
//            if (e instanceof UserPasswordNotMatchException) {
//                throw e;
//            }
//            throw new UserPasswordNotMatchException();
//        }
//        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
//
//        // 生成token
//        return tokenService.createToken(loginUser);
//    }
//
//    /**
//     * 登出
//     *
//     * @param token token
//     */
//    public void logout(String token) {
//        if (StringUtils.isNotEmpty(token)) {
//            // 删除用户缓存记录
//            tokenService.delLoginUser(token);
//        }
//    }
//
//    /**
//     * 刷新token
//     *
//     * @param token token
//     * @return 新的token
//     */
//    public String refreshToken(String token) {
//        LoginUser loginUser = tokenService.getLoginUser(token);
//        if (loginUser != null) {
//            // 验证token是否快过期
//            // if (tokenService.verifyToken(loginUser)) {
//                return tokenService.createToken(loginUser);
//            // }
//        }
//        return null;
//    }
//}
