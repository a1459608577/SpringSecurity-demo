package com.ksn.config;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/29 21:31
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    AuthenticationProvider authenticationProvider;
    @Autowired
    SingleSignOutFilter singleSignOutFilter;
    @Autowired
    LogoutFilter logoutFilter;
    @Autowired
    CasAuthenticationFilter casAuthenticationFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/**").hasRole("user")
                .antMatchers("/login/cas").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilter(casAuthenticationFilter)
                .addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
                .addFilterBefore(logoutFilter, CasAuthenticationFilter.class);

    }
}
