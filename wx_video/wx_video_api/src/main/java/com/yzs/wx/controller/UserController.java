package com.yzs.wx.controller;

import com.yzs.wx.pojo.Users;
import com.yzs.wx.pojo.UsersReport;
import com.yzs.wx.pojo.vo.PublisherVideo;
import com.yzs.wx.pojo.vo.UsersVO;
import com.yzs.wx.service.UserService;
import com.yzs.wx.utils.WXJSONResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/user")
public class UserController {

    //文件路径前缀
    @Value("${image.filepath.prefix}")
    private String prefix;

    @Autowired
    private UserService userService;

    @PostMapping("/uploadFace")
    public WXJSONResult uploadFace(String userid, @RequestParam("file") MultipartFile[] files)throws Exception{
        if(StringUtils.isBlank(userid)){
            return WXJSONResult.errorMsg("userid不能为空！");
        }
        if(files==null||files.length==0){
            return WXJSONResult.errorMsg("图片不能为空！");
        }
        String path = userid+"/face/";

        FileOutputStream outputStream=null;
        InputStream inputStream = null;
        try {
            String filename = files[0].getOriginalFilename();

            if(StringUtils.isNotBlank(filename)){
                String sqlpath = path+filename;
                String finalpath = prefix+path+filename;
                File file = new File(finalpath);
                if(file.getParentFile()!=null||!file.getParentFile().isDirectory()){
                    file.getParentFile().mkdirs();
                }
                outputStream = new FileOutputStream(finalpath);

                inputStream = files[0].getInputStream();
                IOUtils.copy(inputStream,outputStream);
                userService.uploadFace(userid,sqlpath);
                return WXJSONResult.ok(sqlpath);
            }


        }catch (Exception e){

            e.printStackTrace();
            return WXJSONResult.errorMsg("上传出错...");
        }finally {
            if(outputStream!=null){
                outputStream.close();
            }
        }
        return WXJSONResult.ok();

    }

    @PostMapping("/query")
    public WXJSONResult query(String userid,String fanId) throws Exception {
        if(StringUtils.isBlank(userid)){
            return WXJSONResult.errorMsg("userid不能为空！");
        }

        Users user = userService.query(userid);
        UsersVO vo = new UsersVO();
        BeanUtils.copyProperties(user,vo);
        boolean flag = userService.queryIfFollow(userid, fanId);
        vo.setFollow(flag);
        return WXJSONResult.ok(vo);
    }
    @PostMapping("/queryPublisher")
    public WXJSONResult queryPublisher(String loginUserId, String videoId,
                                          String publishUserId) throws Exception {

        if (StringUtils.isBlank(publishUserId)) {
            return WXJSONResult.errorMsg("");
        }

        // 1. 查询视频发布者的信息
        Users userInfo = userService.queryUserInfo(publishUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo, publisher);

        // 2. 查询当前登录者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

        // 3. 查询当前登录者是否已关注视频发布者
        boolean flag = userService.queryIfFollow(publishUserId,loginUserId);
        if(flag){
            publisher.setFollow(true);
        }else {
            publisher.setFollow(false);
        }

        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return WXJSONResult.ok(bean);
    }

    @PostMapping("/beyourfans")
    public WXJSONResult beyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return WXJSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId, fanId);

        return WXJSONResult.ok("关注成功...");
    }

    @PostMapping("/dontbeyourfans")
    public WXJSONResult dontbeyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return WXJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId, fanId);

        return WXJSONResult.ok("取消关注成功...");
    }

    @PostMapping("/reportUser")
    public WXJSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception {

        // 保存举报信息
        userService.reportUser(usersReport);

        return WXJSONResult.errorMsg("举报成功...有你平台变得更美好...");
    }
}
