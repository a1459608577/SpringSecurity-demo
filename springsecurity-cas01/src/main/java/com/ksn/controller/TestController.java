package com.ksn.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/25 17:16
 */
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
    @GetMapping("/user/hello")
    public String user() {
        return "user";
    }

}
