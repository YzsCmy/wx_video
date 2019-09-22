package com.yzs.wx.service;

import com.yzs.wx.pojo.Bgm;
import com.yzs.wx.pojo.Comments;
import com.yzs.wx.pojo.Videos;
import com.yzs.wx.utils.PagedResult;

import java.util.List;

public interface VideoService {
    String uploadVideo(Videos video, String sqlpath);

    void updateVideo(String videoId, String uploadPathDB);

    /**
     * @Description: 分页查询视频列表
     */
    PagedResult getAllVideos(Videos video, Integer isSaveRecord,
                                    Integer page, Integer pageSize);

    List<String> getHotwords();


    /**
     * @Description: 查询我喜欢的视频列表
     */
    PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    /**
     * @Description: 查询我关注的人的视频列表
     */
    PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);

    /**
     * @Description: 用户喜欢/点赞视频
     */
    void userLikeVideo(String userId, String videoId, String videoCreaterId);

    /**
     * @Description: 用户不喜欢/取消点赞视频
     */
    void userUnLikeVideo(String userId, String videoId, String videoCreaterId);

    /**
     * @Description: 用户留言
     */
    public void saveComment(Comments comment);

    /**
     * @Description: 留言分页
     */
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);

    Videos query(String videoId);

    PagedResult queryMy(String userId, Integer page, int pageSize);

    PagedResult getFirstComments(String videoId, Integer page, Integer pageSize);

    PagedResult getSecondComments(String videoId,String fid);

    void addBgm(Bgm bgm);

    PagedResult queryBgmList(Integer page, Integer pageSize);

    void deleteBgm(String id);

    PagedResult queryReportList(Integer page, Integer pageSize);

    void updateVideoStatus(String videoId, Integer status);
}
