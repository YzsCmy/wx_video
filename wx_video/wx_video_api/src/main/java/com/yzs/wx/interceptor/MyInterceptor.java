package com.yzs.wx.interceptor;

import com.yzs.wx.utils.JsonUtils;
import com.yzs.wx.utils.RedisOperator;
import com.yzs.wx.utils.WXJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MyInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisOperator redisOperator;
    public static  final String USER_REDIS_SESSION = "user_redis_session";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String userid = httpServletRequest.getHeader("headerUserId");
        String userToken = httpServletRequest.getHeader("headerUserToken");
        if(StringUtils.isNotBlank(userid)&&StringUtils.isNotBlank(userToken)){
            String token = redisOperator.get(USER_REDIS_SESSION + ":" + userid);
            if(StringUtils.isBlank(token)){
                //重新登录
                returnErrorResponse(httpServletResponse, new WXJSONResult().errorTokenMsg("请登录..."));
                return false;
            }else {
                if(token.equals(userToken)){
                    //验证成功
                    return true;
                }else {
                    //账号在其他地方登录
                    returnErrorResponse(httpServletResponse, new WXJSONResult().errorTokenMsg("请登录..."));
                    return false;
                }
            }
        }else {
            //未登录
            returnErrorResponse(httpServletResponse, new WXJSONResult().errorTokenMsg("请登录..."));
            return false;
        }
    }
    private void returnErrorResponse(HttpServletResponse response, WXJSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
