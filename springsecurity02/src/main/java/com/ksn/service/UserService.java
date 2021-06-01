package com.ksn.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ksn.entity.User;
import com.ksn.mapper.RoleMapper;
import com.ksn.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/26 10:11
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByName(username);
        if (user == null) {
            log.error("用户【{}】不存在",username);
            throw new RuntimeException("用户不存在");
        }
        return user;
    }
}
