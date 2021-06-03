package com.ksn.service;

import cn.hutool.core.util.StrUtil;
import com.ksn.entity.AuthClientDetails;
import com.ksn.mapper.AuthClientDetailsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/6/1 15:12
 */
@Service
@Slf4j
public class AuthClientDetailsService extends JdbcClientDetailsService {

    @Autowired
    AuthClientDetailsMapper authClientDetailsMapper;

    public AuthClientDetailsService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        if (StrUtil.isEmpty(clientId)) {
            throw new RuntimeException("客户端id为空");
        }
        AuthClientDetails details = authClientDetailsMapper.loadClientDetailsByClientId(clientId);

        if (details == null) {
            throw new RuntimeException("应用信息为空");
        }

        return details;
    }
}
