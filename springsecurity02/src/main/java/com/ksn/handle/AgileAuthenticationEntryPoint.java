package com.ksn.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/29 21:47
 */
@Slf4j
@Component
public class AgileAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     *  * AccessDeniedHandler: 用来解决认证过的用户访问无权限资源时的异常， 登录过的用户出现异常进这个
     *  * AuthenticationEntryPoint: 用来解决匿名用户访问无权限资源时的异常,没登录的用户出现异常进这个,没登录的用户出现异常进这个,就是用户不登录去访问需要登录的接口会进这个地方
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 认证入口点,
        log.error("匿名用户鉴权失败");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write("匿名用户鉴权失败");
        writer.flush();
        writer.close();
    }
}
