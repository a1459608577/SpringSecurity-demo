package com.ksn.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/31 16:56
 */
@Controller
@RequestMapping("/authorize")
public class AuthController {

    @ResponseBody
    @GetMapping("/code")
    public String code(@RequestParam("code") String code, @RequestParam("state") String state) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("client_id", "cd2857419595e06f3721");
        map.put("client_secret", "c07f67405d1563c4d785dc599e9091c1e333e6c6");
        map.put("state", "ksn");
        map.put("code", code);
        map.put("redirect_uri", "http://127.0.0.1:8003/authorize/code");
        String s = JSONUtil.toJsonStr(map);

        HttpResponse execute = HttpUtil.createPost("https://github.com/login/oauth/access_token").body(s).execute();
        System.out.println(execute.body());

        String token = "token " + execute.body().substring(execute.body().indexOf("=") + 1, execute.body().indexOf("&"));
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Authorization", token);
        HttpResponse execute1 = HttpUtil.createGet("https://api.github.com/user").addHeaders(stringStringHashMap).execute();

        return execute1.body();
    }

    @ResponseBody
    @GetMapping("/test")
    public String test() {
        return "success";
    }
}
