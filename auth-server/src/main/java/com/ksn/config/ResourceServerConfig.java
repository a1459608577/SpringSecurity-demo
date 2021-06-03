package com.ksn.config;

import com.ksn.handle.*;
import jdk.nashorn.internal.runtime.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;

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
    @Autowired
    private AuthenticationManager authenticationManagerBean;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("res1").tokenStore(tokenStore);

        resources.accessDeniedHandler(agileAccessDeniedHandler).authenticationEntryPoint(agileAuthenticationEntryPoint);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.formLogin().successHandler(agileAuthticationSuccessHandler).failureHandler(agileAuthenticationFailHandler)
                // 配置表单登录设置请求的username和password
                .usernameParameter("an").passwordParameter("ap");

        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .and()
                .csrf().disable();

        http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }

    @Bean
    public Filter clientCredentialsTokenEndpointFilter(){
        agileAuthenticationEntryPoint.setExceptionRenderer(new AgileOAuth2ExceptionRenderer());
        agileAccessDeniedHandler.setExceptionRenderer(new AgileOAuth2ExceptionRenderer());
        ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter=new ClientCredentialsTokenEndpointFilter();
        clientCredentialsTokenEndpointFilter.setAuthenticationEntryPoint(agileAuthenticationEntryPoint);
        clientCredentialsTokenEndpointFilter.setAuthenticationFailureHandler(agileAuthenticationFailHandler);
        clientCredentialsTokenEndpointFilter.setAuthenticationSuccessHandler(agileAuthticationSuccessHandler);
        clientCredentialsTokenEndpointFilter.setAuthenticationManager(authenticationManagerBean);
        return clientCredentialsTokenEndpointFilter;
    }
}
