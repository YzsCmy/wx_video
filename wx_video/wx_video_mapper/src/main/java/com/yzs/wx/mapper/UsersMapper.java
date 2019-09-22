package com.yzs.wx.mapper;

import com.yzs.wx.utils.MyMapper;
import com.yzs.wx.pojo.Users;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface UsersMapper extends MyMapper<Users> {

    /**
     * @Description: 用户受喜欢数累加
     */
    void addReceiveLikeCount(String userId);

    /**
     * @Description: 用户受喜欢数累减
     */
    void reduceReceiveLikeCount(String userId);

    /**
     * @Description: 更新用户头像存储路径
     * @param path
     * @param userid
     */
    @Update("update users set face_image=#{path} where id=#{userid}")
    void updateFaceUrlByUserId(@Param("path") String path, @Param("userid") String userid);

    /**
     * @Description: 增加粉丝数
     */
    void addFansCount(String userId);

    /**
     * @Description: 增加关注数
     */
    void addFollersCount(String userId);

    /**
     * @Description: 减少粉丝数
     */
    void reduceFansCount(String userId);

    /**
     * @Description: 减少关注数
     */
    void reduceFollersCount(String userId);


}