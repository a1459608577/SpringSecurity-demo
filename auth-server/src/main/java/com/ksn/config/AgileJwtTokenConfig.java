package com.ksn.config;

import cn.hutool.crypto.SecureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/30 21:26
 */
@Configuration
public class AgileJwtTokenConfig {

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        // 非对称加密
        converter.setKeyPair(SecureUtil.generateKeyPair("RSA", 512));
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            // 自定义token携带的信息
            Map<String, Object> map = Collections.synchronizedMap(new HashMap<>());
            map.put("GlobalConstants.PLATFORM_NAME_KEY", "GlobalConstants.PLATFORM_NAME_EN");

            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            return accessToken;
        };
    }

    /**
     * 配置token存放位置，可以是内存，redis也可以结合jwt一起使用
     * @return
     */
    @Bean
    TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }
}
