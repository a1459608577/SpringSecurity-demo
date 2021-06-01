package com.ksn.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义权限处理器，403没有权限的会进这个handler，同时还有登录成功，登录失败，退出登录等handler
 * LogoutSuccessHandler 登出成功处理器
 * 登录失败处理器： 继承 SimpleUrlAuthenticationFailureHandler 或者实现 AuthenticationFailureHandler
 *
 * AccessDeniedHandler: 用来解决认证过的用户访问无权限资源时的异常， 登录过的用户出现异常进这个
 * AuthenticationEntryPoint: 用来解决匿名用户访问无权限资源时的异常,没登录的用户出现异常进这个,就是用户不登录去访问需要登录的接口会进这个地方
 * @author ksn
 * @version 1.0
 * @date 2021/5/27 15:53
 */
@Slf4j
@Component
public class AgileAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("鉴权失败");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write("鉴权失败");
        writer.flush();
        writer.close();
    }
}
