package com.ksn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/25 17:32
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 用来忽略的地址，不拦截
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    /**
     * successForwardUrl和defaultSuccessUrl的区别是：
     *      successForwardUrl: 只要登录成功就跳转到这个页面
     *      defaultSuccessUrl：当第二个参数为true的时候，效果同上，默认为false，当第一次登录是带着页面的如127.0.0.1:8080/hello，
     *      则登录成功后会跳转到hello页面，而不是index。
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/test/test02").hasRole("admin")
                .antMatchers("/test/test03").hasRole("user")
                .anyRequest().authenticated()
                .and()
                // 设置登录页面
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                // 配置提交过来的username和password
                .usernameParameter("name")
                .passwordParameter("pwd")
                // 设置登录成功后跳转的地址
                .successForwardUrl("/index.html")
//                .defaultSuccessUrl("/index", true)
//                 配置登录失败后跳转的页面
                .failureForwardUrl("/fail.html")
//                .failureUrl("/fail")
                // 配置登录成功后返回的数据
                .successHandler((req, res, authentication) -> {
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    PrintWriter writer = res.getWriter();
                    writer.write("success成功" + authentication.getPrincipal());
                    writer.flush();
                    writer.close();
                })
                // 表示登录相关的页面/接口不要被拦截。
                .permitAll()
                .and()
                .logout()
                // 可以通过这个来修改注销的url
                .logoutUrl("/logout")
                // 同上
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                // 注销成功后页面
                .logoutSuccessUrl("/index.html")
                .logoutSuccessHandler((req, res, authentication) -> {
                    res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    PrintWriter writer = res.getWriter();
                    writer.write("登出success成功" + authentication.getPrincipal());
                    writer.flush();
                    writer.close();
                })
                .deleteCookies()
                .and()
                // 关闭csrf
                .csrf().disable();
                // 配置这个是当访问需要认证后的资源时不会重定向到登录页面，而是返回json
//                .exceptionHandling()
//                .authenticationEntryPoint((req, res, authException) -> {
//                    res.setContentType("application/json;charset=utf-8");
//                    PrintWriter out = res.getWriter();
//                    out.write("尚未登录，请先登录");
//                    out.flush();
//                    out.close();
//                });
    }

    /**
     * 角色继承， 表示admin拥有user角色全部的权限，要手动加上ROLE_
     * @return
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        // 配置多个用户
        builder.inMemoryAuthentication()
                .withUser("admin1")
                .password("123")
                .roles("admin")
                .and()
                .withUser("user1")
                .password("123")
                .roles("user");
    }

}
