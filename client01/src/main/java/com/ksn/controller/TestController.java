package com.ksn.controller;

import org.springframework.web.bind.annotation.GetMapping;
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
        int a = 1/0;
        return "hello";
    }
    @GetMapping("/admin/hello")
    public String admin() {
        return "admin";
    }

}
