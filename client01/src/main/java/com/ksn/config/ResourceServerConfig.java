package com.ksn.config;

import com.ksn.handle.AgileAccessDeniedHandler;
import com.ksn.handle.AgileAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/6/1 9:24
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private AgileAccessDeniedHandler agileAccessDeniedHandler;
    @Autowired
    private AgileAuthenticationEntryPoint agileAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("res1").tokenStore(tokenStore());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.exceptionHandling().accessDeniedHandler(agileAccessDeniedHandler).authenticationEntryPoint(agileAuthenticationEntryPoint);

        http.authorizeRequests().antMatchers("/admin/**").hasRole("admin").anyRequest().authenticated();
    }
}
