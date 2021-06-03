package com.ksn.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/30 15:16
 */
// 表示开启授权服务器自动化配置
@EnableAuthorizationServer
@Configuration
@Slf4j
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

//    @Autowired
//    ClientDetailsService clientDetailsService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    DataSource dataSource;
    @Autowired
    JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    TokenEnhancer tokenEnhancer;
    @Autowired
    TokenStore tokenStore;
//    @Autowired
//    ClientDetailsService clientDetailsService;


        // 从数据中查询客户端信息
        @Bean("clientDetailsService1")
        public ClientDetailsService clientDetailsService() {
            return new JdbcClientDetailsService(dataSource);
        }

    /**
     * 用来配置token的一些基本信息，
     * @return
     */
    @Bean
    AuthorizationServerTokenServices authorizationServerTokenServices() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        // 告诉spring security把自定义token生产方式加入到TokenEnhancerChain中
        tokenEnhancerChain.setTokenEnhancers(CollUtil.newArrayList(tokenEnhancer, jwtAccessTokenConverter));
        DefaultTokenServices services = new DefaultTokenServices();
        services.setClientDetailsService(clientDetailsService());
        // 是否支持刷新
        services.setSupportRefreshToken(true);
        // 存储位置
        services.setTokenStore(tokenStore);
        // 刷新 token时把refresh_token也刷新
        services.setReuseRefreshToken(true);
        // 将自定义的token生产方式加到TokenEnhancer中
        services.setTokenEnhancer(tokenEnhancerChain);
        // 过期时间
//        services.setAccessTokenValiditySeconds(60 * 60 *2);
        // 刷新令牌过期时间
//        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
        return services;
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        // 表示授权码存在内存中
        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * 配置认证服务的异常处理,认证的时候出的异常都会走这里
     * @return
     */
    @Bean
    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {
            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                log.error("Authorization Exception Transform：", e);
                ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
                return new ResponseEntity(responseEntity.getStatusCode().value() + "--" + formatExceptionMsg(e) + "--" + e.getClass().getSimpleName()
                        ,responseEntity.getHeaders(), HttpStatus.OK);
            }
        };
    }

    /**
     * 异常信息处理响应信息
     *
     * @param e
     * @return
     */
    private String formatExceptionMsg(Exception e) {
        if (e instanceof InvalidGrantException) {
            return "用户名或密码不正确";
        } else if (e instanceof Exception) {
            return e.getMessage();
        }

        //TODO 处理其他异常

        return "Authorization Failed";
    }

    /**
     * 用来配置令牌端点的安全约束，就是说谁能访问，谁不能访问
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //开启/oauth/token_key验证端口无权限访问
        security.tokenKeyAccess("permitAll()");
        //开启/oauth/check_token验证端认证权限访问
        security.checkTokenAccess("isAuthenticated()");
        // 允许表单登录
        security.allowFormAuthenticationForClients();
    }

    /**
     * 授权码模式需要校验用户和客户端，这里就是校验客户端，可以从数据库查，类似UserDetailsService
     *
     * refresh_token： 刷新token
     * authorization_code: 授权码模式
     * implicit： 简单模式，配置简单模式，只要在authorizedGrantTypes中加上implicit即可
     * password: 密码模式
     * client_credentials: 客户端模式
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory()
//                .withClient("ksn_client")
//                .secret(new BCryptPasswordEncoder().encode("ksn_secret"))
//                .resourceIds("res1")
//                .authorizedGrantTypes("authorization_code", "refresh_token", "implicit", "password", "client_credentials")
//                .scopes("all")
//                .redirectUris("http://www.baidu.com");
            clients.withClientDetails(clientDetailsService());
    }

    /**
     * 用来配置令牌的访问端点和令牌服务，authorizationCodeServices用来配置授权码的存储位置，这里是先存在内存中
     * authorizationServerTokenServices是用来配置token的存储位置，token是用来获取资源的，授权码是用来获取token的用一次就失效
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 授权码模式，简单模式
//        endpoints.authorizationCodeServices(authorizationCodeServices())
//                .tokenServices(authorizationServerTokenServices());

        // 密码模式
        // 配置认证管理器
        endpoints.authenticationManager(authenticationManager)
                // 配置jwt转换器， 定义token的生成方式
                .accessTokenConverter(jwtAccessTokenConverter)
                // 配置token信息
                .tokenServices(authorizationServerTokenServices())
                // 配置token管理方式
                .tokenStore(tokenStore)
                // 配置响应异常处理
                .exceptionTranslator(webResponseExceptionTranslator())
                .allowedTokenEndpointRequestMethods(HttpMethod.OPTIONS, HttpMethod.GET, HttpMethod.POST);
    }
}
