package com.ksn.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理器，继承SavedRequestAwareAuthenticationSuccessHandler而不是实现AuthenticationSuccessHandler接口
 * 是因为这个类会记住上一次请求 的url方便登录成功后直接跳转
 * @author ksn
 * @version 1.0
 * @date 2021/5/27 17:29
 */
@Slf4j
@Component
public class AgileAuthticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        log.info("登录成功。。。。。");
        super.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void setRequestCache(RequestCache requestCache) {
        super.setRequestCache(requestCache);
    }
}
