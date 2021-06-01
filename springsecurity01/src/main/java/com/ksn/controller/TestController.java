package com.ksn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/25 17:16
 */
@Controller
@RequestMapping("test")
public class TestController {

    @GetMapping("test01")
    @ResponseBody
    public String test01() {
        return "test01 + all";
    }

    @GetMapping("test02")
    @ResponseBody
    public String test02() {
        return "test02 + admin";
    }

    @GetMapping("test03")
    @ResponseBody
    public String test03() {
        return "test03 + user";
    }
}
