package com.yzs.wx.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yzs.wx.mapper.UsersFansMapper;
import com.yzs.wx.mapper.UsersLikeVideosMapper;
import com.yzs.wx.mapper.UsersMapper;
import com.yzs.wx.mapper.UsersReportMapper;
import com.yzs.wx.pojo.*;
import com.yzs.wx.service.UserService;
import com.yzs.wx.utils.MD5Utils;
import com.yzs.wx.utils.PagedResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UsersFansMapper usersFansMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UsersReportMapper usersReportMapper;
    @Autowired
    private Sid sid;

    @Override
    public boolean isExist(Users user) {

        UsersExample usersExample = new UsersExample();
        UsersExample.Criteria criteria = usersExample.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        Users one = usersMapper.selectOneByExample(usersExample);
        return one!=null;
    }

    @Override
    public void regist(Users user) throws Exception{

        user.setNickname(user.getUsername());
        user.setFansCounts(0);
        user.setFollowCounts(0);
        user.setReceiveLikeCounts(0);
        user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
        user.setId(sid.nextShort());
        usersMapper.insert(user);
    }

    @Override
    public Users login(Users user) throws Exception {

        String passw = MD5Utils.getMD5Str(user.getPassword());
        UsersExample usersExample = new UsersExample();
        UsersExample.Criteria criteria = usersExample.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        criteria.andPasswordEqualTo(passw);
        Users users = usersMapper.selectOneByExample(usersExample);

        return users;
    }

    @Override
    public void uploadFace(String userid, String sqlpath) {
        usersMapper.updateFaceUrlByUserId(sqlpath,userid);
    }

    @Override
    public Users query(String userid) {
        return usersMapper.selectByPrimaryKey(userid);
    }

    @Override
    public Users queryUserInfo(String userId) {
        if(StringUtils.isBlank(userId)){
            return null;
        }
        UsersExample usersExample = new UsersExample();
        UsersExample.Criteria criteria = usersExample.createCriteria();
        criteria.andIdEqualTo(userId);
        Users user = usersMapper.selectOneByExample(usersExample);
        return user;
    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {

        if(StringUtils.isBlank(userId)){
            return false;
        }
        UsersFansExample example = new UsersFansExample();
        UsersFansExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andFanIdEqualTo(fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);

        if (list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }

        return false;
    }

    @Override
    public PagedResult queryUsers(Users user, Integer page, Integer pageSize) {

        String username = "";
        String nickname = "";
        if (user != null) {
            username = user.getUsername();
            nickname = user.getNickname();
        }

        PageHelper.startPage(page, pageSize);

        UsersExample userExample = new UsersExample();
        UsersExample.Criteria userCriteria = userExample.createCriteria();
        if (StringUtils.isNotBlank(username)) {
            userCriteria.andUsernameLike("%" + username + "%");
        }
        if (StringUtils.isNotBlank(nickname)) {
            userCriteria.andNicknameLike("%" + nickname + "%");
        }

        List<Users> userList = usersMapper.selectByExample(userExample);

        PageInfo<Users> pageList = new PageInfo<Users>(userList);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(userList);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());

        return grid;
    }

    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
            return false;
        }

        UsersLikeVideosExample example = new UsersLikeVideosExample();
        UsersLikeVideosExample.Criteria criteria = example.createCriteria();

        criteria.andUserIdEqualTo(userId);
        criteria.andVideoIdEqualTo(videoId);

        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);

        if (list != null && list.size() >0) {
            return true;
        }

        return false;
    }

    @Override
    public void saveUserFanRelation(String userId, String fanId) {

        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fanId);

        UsersFans usersFans = new UsersFans();
        usersFans.setId(sid.nextShort());
        usersFans.setFanId(fanId);
        usersFans.setUserId(userId);
        usersFansMapper.insert(usersFans);

    }

    @Override
    public void deleteUserFanRelation(String userId, String fanId) {

        UsersFansExample example = new UsersFansExample();
        UsersFansExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andFanIdEqualTo(fanId);

        usersFansMapper.deleteByExample(example);

        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fanId);
    }

    @Override
    public void reportUser(UsersReport userReport) {

        String urId = sid.nextShort();
        userReport.setId(urId);
        userReport.setCreateDate(new Date());

        usersReportMapper.insert(userReport);

    }
}
