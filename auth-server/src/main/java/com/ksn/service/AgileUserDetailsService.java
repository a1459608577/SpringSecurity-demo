package com.ksn.service;

import cn.hutool.core.util.StrUtil;
import com.ksn.entity.CustomizeUser;
import com.ksn.mapper.CustomizeUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/6/1 13:39
 */
@Slf4j
@Service
public class AgileUserDetailsService implements UserDetailsService {

    @Autowired
    CustomizeUserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (StrUtil.isEmpty(s)) {
            throw  new RuntimeException("用户名不存在");
        }
        CustomizeUser customizeUser = mapper.selectByName(s);
        if (customizeUser == null ) {
            throw  new RuntimeException("用户名或密码不正确");
        }
        return customizeUser;
    }
}
