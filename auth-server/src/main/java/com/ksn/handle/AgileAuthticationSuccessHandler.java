package com.ksn.handle;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/6/1 9:39
 * 登录成功处理器
 */
@Slf4j
@Component
public class AgileAuthticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (StrUtil.isEmpty(authorization)) {
            throw new RuntimeException("\"认证信息为空");
        }
        log.info("登录成功");
        super.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void setRequestCache(RequestCache requestCache) {
        super.setRequestCache(requestCache);
    }
}
