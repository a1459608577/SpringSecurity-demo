package com.ksn.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * 自定义过滤器(会重复执行，有问题)
 * @author ksn
 * @version 1.0
 * @date 2021/5/27 14:20
 */
//@Component
@Slf4j
public class VerifyCodeFilter extends OncePerRequestFilter  {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String code = httpServletRequest.getParameter("code");
        if (code == null || !code.equals("asdf")) {
            log.error("验证码不对");
        }
        System.out.println("过滤器执行了");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
