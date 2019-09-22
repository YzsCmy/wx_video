package com.yzs.wx.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping("/hello")
    public String toIndex(){
        System.out.println("/index  running......");
        return "hello";
    }
    @RequestMapping("/login")
    public String tndex(){
        return "login";
    }

    @RequestMapping("/center")
    public String index(){
        return "center";
    }


}
