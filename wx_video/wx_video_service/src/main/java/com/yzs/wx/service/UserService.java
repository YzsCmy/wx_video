package com.yzs.wx.service;

import com.yzs.wx.pojo.Users;
import com.yzs.wx.pojo.UsersReport;
import com.yzs.wx.utils.PagedResult;

public interface UserService {

    boolean isExist(Users user);

    void regist(Users user) throws Exception;

    Users login(Users user) throws Exception;

    void uploadFace(String userid, String sqlpath);

    Users query(String userid);


    /**
     * @Description: 查询用户信息
     */
    public Users queryUserInfo(String userId);
    /**
     * @Description: 查询用户是否关注
     */
    public boolean queryIfFollow(String userId, String fanId);

    PagedResult queryUsers(Users user, Integer page, Integer pageSize);

    /**
     * @Description: 查询用户是否喜欢点赞视频
     */
    public boolean isUserLikeVideo(String userId, String videoId);


    /**
     * @Description: 增加用户和粉丝的关系
     */
    public void saveUserFanRelation(String userId, String fanId);

    /**
     * @Description: 删除用户和粉丝的关系
     */
    public void deleteUserFanRelation(String userId, String fanId);

    /**
     * @Description: 举报用户
     */
    public void reportUser(UsersReport userReport);

}
