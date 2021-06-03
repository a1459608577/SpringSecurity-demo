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
import org.springframework.stereotype.Service;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/6/1 15:12
 */
@Service
@Slf4j
public class AuthClientDetailsService implements ClientDetailsService {

    @Autowired
    AuthClientDetailsMapper authClientDetailsMapper;

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
