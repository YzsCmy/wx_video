package com.yzs.wx.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yzs.wx.enums.BGMOperatorTypeEnum;
import com.yzs.wx.mapper.*;
import com.yzs.wx.pojo.*;
import com.yzs.wx.pojo.vo.CommentsVO;
import com.yzs.wx.pojo.vo.Reports;
import com.yzs.wx.pojo.vo.VideosVO;
import com.yzs.wx.service.VideoService;
import com.yzs.wx.utils.JsonUtils;
import com.yzs.wx.utils.PagedResult;
import com.yzs.wx.utils.TimeAgoUtils;
import com.yzs.wx.utils.ZKCurator;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VideoServiceImpl implements VideoService {

    @Autowired
    private ZKCurator zkCurator;

    @Autowired
    private BgmMapper bgmMapper;
    @Autowired
    private UsersReportMapperCustom usersReportMapperCustom;

    @Autowired
    private CommentsMapper commentMapper;
    @Autowired
    private CommentsMapperCustom commentMapperCustom;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;
    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private Sid sid;


    @Override
    public String uploadVideo(Videos video, String sqlpath) {

        String id = sid.nextShort();
        video.setCreateTime(new Date());
        video.setId(id);
        video.setStatus(1);
        video.setVideoPath(sqlpath);
        videosMapper.insertSelective(video);
        return id;
    }

    @Override
    public void updateVideo(String videoId, String uploadPathDB) {
        videosMapper.updateCoverPathById(videoId,uploadPathDB);
    }


    public PagedResult getAllVideos(Videos video, Integer isSaveRecord,
                                    Integer page, Integer pageSize) {

         //保存热搜词
        String desc = video.getVideoDesc();
        String userId = video.getUserId();
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords record = new SearchRecords();
            String recordId = sid.nextShort();
            record.setId(recordId);
            record.setContent(desc);
            searchRecordsMapper.insert(record);
        }

        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc, userId);
        //List<VideosVO> list = videosMapperCustom.queryAllVideos(null, null);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Override
    public List<String> getHotwords() {
        return searchRecordsMapper.getHotwords();
    }

    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        // 1. 保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);

        // 2. 视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

        // 3. 用户受喜欢数量的累加
        usersMapper.addReceiveLikeCount(videoCreaterId);
    }

    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
        // 1. 删除用户和视频的喜欢点赞关联关系表

        UsersLikeVideosExample usersLikeVideosExample = new UsersLikeVideosExample();
        UsersLikeVideosExample.Criteria criteria = usersLikeVideosExample.createCriteria();

        criteria.andUserIdEqualTo(userId);
        criteria.andVideoIdEqualTo(videoId);

        usersLikeVideosMapper.deleteByExample(usersLikeVideosExample);

        // 2. 视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);

        // 3. 用户受喜欢数量的累减
        usersMapper.reduceReceiveLikeCount(videoCreaterId);

    }

    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Override
    public Videos query(String videoId) {
        return videosMapper.selectByPrimaryKey(videoId);
    }

    @Override
    public PagedResult queryMy(String userId, Integer page, int pageSize) {

        Videos videos = new Videos();

        videos.setUserId(userId);
        PagedResult result = getAllVideos(videos, null, page, pageSize);

        return result;
    }

    @Override
    public void saveComment(Comments comment) {

        if(StringUtils.isNotBlank(comment.getFatherCommentId())){
            commentMapper.updateHasChild(comment.getFatherCommentId());
        }
        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentMapper.insert(comment);
    }

    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        List<CommentsVO> list = commentMapperCustom.queryComments(videoId);

        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());

        return grid;
    }

    @Override
    public PagedResult getFirstComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        List<CommentsVO> list = commentMapperCustom.queryFirstComments(videoId);

        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        Integer count = commentMapper.count(videoId);

        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(count);

        return grid;
    }
    @Override
    public PagedResult getSecondComments(String videoId, String fid) {
        //PageHelper.startPage(page, pageSize);

        if(StringUtils.isBlank(fid)){
            throw new RuntimeException(" getSecondComments  fid is empty!");
        }
        List<CommentsVO> list = commentMapperCustom.querySecondComments(videoId,fid);

        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        //PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(1);
        grid.setRows(list);
        grid.setPage(1);
        grid.setRecords(list.size());

        return grid;
    }

    @Override
    public PagedResult queryReportList(Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        List<Reports> reportsList = usersReportMapperCustom.selectAllVideoReport();

        PageInfo<Reports> pageList = new PageInfo<Reports>(reportsList);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(reportsList);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());

        return grid;
    }

    @Override
    public void updateVideoStatus(String videoId, Integer status) {

        Videos video = new Videos();
        video.setId(videoId);
        video.setStatus(status);
        videosMapper.updateByPrimaryKeySelective(video);
    }

    @Override
    public PagedResult queryBgmList(Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        BgmExample example = new BgmExample();
        List<Bgm> list = bgmMapper.selectByExample(example);

        PageInfo<Bgm> pageList = new PageInfo<>(list);

        PagedResult result = new PagedResult();
        result.setTotal(pageList.getPages());
        result.setRows(list);
        result.setPage(page);
        result.setRecords(pageList.getTotal());

        return result;
    }

    @Override
    public void addBgm(Bgm bgm) {
        String bgmId = sid.nextShort();
        bgm.setId(bgmId);
        bgmMapper.insert(bgm);

        Map<String, String> map = new HashMap<>();
        map.put("operType", BGMOperatorTypeEnum.ADD.type);
        map.put("path", bgm.getPath());

        zkCurator.sendBgmOperator(bgmId, JsonUtils.objectToJson(map));
    }

    @Override
    public void deleteBgm(String id) {
        Bgm bgm = bgmMapper.selectByPrimaryKey(id);

        bgmMapper.deleteByPrimaryKey(id);

        Map<String, String> map = new HashMap<>();
        map.put("operType", BGMOperatorTypeEnum.DELETE.type);
        map.put("path", bgm.getPath());

        zkCurator.sendBgmOperator(id, JsonUtils.objectToJson(map));

    }
}
