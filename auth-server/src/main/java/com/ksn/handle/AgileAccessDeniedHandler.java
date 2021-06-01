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

/**
 * @author ksn
 * @version 1.0
 * @date 2021/6/1 9:49
 * 已登录认证用户权限不足处理器
 */
@Slf4j
@Component
public class AgileAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpServletResponse.getWriter().write("鉴权失败，用户权限不足");
        log.error("Visit 【{}】 Access Denied!Exception Log:",httpServletRequest.getRequestURL().toString(),e);
    }
}
