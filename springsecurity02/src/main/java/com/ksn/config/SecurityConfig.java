package com.ksn.config;

import com.ksn.filter.VerifyCodeFilter;
import com.ksn.handle.*;
import com.ksn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import java.io.PrintWriter;
import java.nio.file.attribute.UserPrincipal;

/**
 * @EnableWebSecurity作用
 * 1. 加载了WebSecurityConfiguration配置类配置安全认证策略。
 * 2: 加载了AuthenticationConfiguration, 配置了认证信息。
 *
 * @author ksn
 * @version 1.0
 * @date 2021/5/25 17:32
 */
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 自定义验证方法
    @Autowired
    private UserService userService;
    // 自定义过滤器
//    @Autowired
//    private VerifyCodeFilter verifyCodeFilter;
    // 自定义权限处理器
    @Autowired
    private AgileAccessDeniedHandler agileAccessDeniedHandler;
    // 自定义验证器
    @Autowired
    private AgileUserDetailsAuthenticationProvider authenticationProvider;
    // 登录成功处理器
    @Autowired
    private AgileAuthticationSuccessHandler agileAuthticationSuccessHandler;
    // 登录失败处理器
    @Autowired
    private AgileAuthenticationFailHandler agileAuthenticationFailHandler;
    // 匿名用户鉴权失败进入
    @Autowired
    private AgileAuthenticationEntryPoint agileAuthenticationEntryPoint;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123"));
    }

    /**
     * 分布式部署情况下，session不能共享，不能实现同时只有一个用户登录的情况，所以使用spring session来解决，首先导入spring session的jar包
     * 然后在引入redis的依赖，然后配置redis地址端口即可
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 在RememberMeAuthenticationFilter过滤器之前执行
         *         http.addFilterBefore(verifyCodeFilter, RememberMeAuthenticationFilter.class);
         *         配置验证器验证用户是否正确
         *         http.authenticationProvider(authenticationProvider);
         */

        http.authorizeRequests()
                .antMatchers("/test/test02").hasRole("admin")
                .antMatchers("/test/test03").hasRole("user")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                .successHandler(agileAuthticationSuccessHandler)
                .failureHandler(agileAuthenticationFailHandler)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    PrintWriter writer = res.getWriter();
                    writer.write("登出成功");
                    writer.flush();
                    writer.close();
                })
                // 退出后清除信息
                .clearAuthentication(true)
                .and()
                // 配置已经登录用户没有权限访问的自定义处理类
                .exceptionHandling().accessDeniedHandler(agileAccessDeniedHandler)
                // 匿名用户没有权限
                .authenticationEntryPoint(agileAuthenticationEntryPoint)
                .and()
                // 配置自定义过滤器
                .addFilterAfter(verifyCodeFilter(), RememberMeAuthenticationFilter.class)
                .csrf().disable()
                // 设置只能一个在线，当一个用户登录后，另一个用户再去登录，第一个登录的用户请求接口就会报错
                // This session has been expired (possibly due to multiple concurrent logins being attempted as the same user).
                .sessionManagement()
                .maximumSessions(1);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 自定义验证规则
        auth.userDetailsService(userService);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return roleHierarchy;
    }

    @Bean
    public VerifyCodeFilter verifyCodeFilter() {
        return new VerifyCodeFilter();
    }

}
