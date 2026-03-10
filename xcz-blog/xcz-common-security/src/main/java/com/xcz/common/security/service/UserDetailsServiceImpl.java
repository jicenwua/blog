//package com.xcz.common.security.service;
//
//import com.xcz.common.core.utils.StringUtils;
//import com.xcz.common.security.extend.LoginUser;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
///**
// * 用户详情服务实现
// * 实现UserDetailsService接口，提供基于Spring Security的用户信息加载机制
// */
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    // 这里可以注入用户服务来查询数据库
//    // @Autowired
//    // private IUserService userService;
//
//    /**
//     * 根据用户名加载用户信息
//     *
//     * @param username 用户名
//     * @return UserDetails
//     * @throws UsernameNotFoundException 用户不存在异常
//     */
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // 在实际应用中，这里应该从数据库或其他数据源加载用户信息
//        // 示例：IUser user = userService.selectUserByUserName(username);
//
//        // 临时返回一个默认用户进行演示
//        // 在真实环境中，应该从数据库获取用户信息
//        if (StringUtils.isEmpty(username)) {
//            throw new UsernameNotFoundException("用户名不能为空");
//        }
//
//        // 示例用户 - 在真实环境中应该从数据库获取
//        // 模拟从数据库获取用户信息
//        // 这里仅作示例，实际应用中需要根据业务需求实现
//        return new LoginUser(username, "encoded_password", true, true, true, true, null);
//    }
//}
