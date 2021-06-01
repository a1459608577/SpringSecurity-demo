package com.ksn.config;

import com.ksn.handle.AgileAccessDeniedHandler;
import com.ksn.handle.AgileAuthenticationEntryPoint;
import com.ksn.handle.AgileAuthenticationFailHandler;
import com.ksn.handle.AgileAuthticationSuccessHandler;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/30 20:55
 */
@Component
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private AgileAuthticationSuccessHandler agileAuthticationSuccessHandler;
    @Autowired
    private AgileAuthenticationFailHandler agileAuthenticationFailHandler;
    @Autowired
    private AgileAccessDeniedHandler agileAccessDeniedHandler;
    @Autowired
    private AgileAuthenticationEntryPoint agileAuthenticationEntryPoint;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("res1").tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.formLogin().successHandler(agileAuthticationSuccessHandler).failureHandler(agileAuthenticationFailHandler)
                // 配置表单登录设置请求的username和password
                .usernameParameter("an").passwordParameter("ap");

        http.exceptionHandling().accessDeniedHandler(agileAccessDeniedHandler).authenticationEntryPoint(agileAuthenticationEntryPoint);

        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .and()
                .csrf().disable();

        http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }
}
