package com.yzs.wx.controller;

import com.yzs.wx.pojo.Users;
import com.yzs.wx.pojo.vo.UsersVO;
import com.yzs.wx.service.UserService;
import com.yzs.wx.utils.RedisOperator;
import com.yzs.wx.utils.WXJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class LoginRegisterController  extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;

    @PostMapping("/regist")
    public WXJSONResult regist(@RequestBody Users user) throws Exception {
        if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())){
            return WXJSONResult.errorMsg("用户名和密码不能为空！");
        }
        if(userService.isExist(user)){
            return WXJSONResult.errorMsg("用户已存在！");
        }
        userService.regist(user);
        user.setPassword("");
        //发送token
        UsersVO vo = generateToken(user);
        return WXJSONResult.ok(vo);
    }
    @PostMapping("/login")
    public WXJSONResult login(@RequestBody Users user) throws Exception {
        if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())){
            return WXJSONResult.errorMsg("用户名和密码不能为空！");
        }

        Users login = userService.login(user);
        if(login!=null){

            login.setPassword("");
            //发送token
            UsersVO vo = generateToken(login);
            return WXJSONResult.ok(vo);
        }
        return WXJSONResult.errorMsg("用户名或密码错误!");
    }

    @GetMapping("/logout")
    public WXJSONResult logout(String userid) throws Exception {
        if(StringUtils.isBlank(userid)){
            return WXJSONResult.errorMsg("userid不能为空！");
        }

        redisOperator.del(USER_REDIS_SESSION+":"+userid);
        return WXJSONResult.ok();
    }

    private UsersVO generateToken(Users model){

        String token = UUID.randomUUID().toString();
        redisOperator.set(USER_REDIS_SESSION+":"+model.getId(),token,1000*60*30);
        UsersVO vo = new UsersVO();
        BeanUtils.copyProperties(model,vo);
        vo.setUserToken(token);
        return vo;
    }
}
