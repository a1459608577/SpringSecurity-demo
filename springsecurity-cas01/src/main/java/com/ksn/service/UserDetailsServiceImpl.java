package com.ksn.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/29 21:28
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // cas登录成功后，用户信息还是从这里得到， 按理是从数据库查，图方便就new一个
        return new User(username, "123", true, true, true,
                true, AuthorityUtils.createAuthorityList("ROLE_user"));
    }
}
