package com.yzs.wx.controller;

import com.yzs.wx.pojo.Bgm;
import com.yzs.wx.service.BgmService;
import com.yzs.wx.utils.WXJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bgm")
public class BgmController {
    @Autowired
    private BgmService bgmService;

    @GetMapping("/list")
    public WXJSONResult list(){
        List<Bgm> list = bgmService.queryAll();
        return WXJSONResult.ok(list);
    }
}
