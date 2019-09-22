package com.yzs.wx.mapper;

import com.yzs.wx.pojo.Comments;
import com.yzs.wx.pojo.vo.CommentsVO;
import com.yzs.wx.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	List<CommentsVO> queryComments(String videoId);
	List<CommentsVO> queryFirstComments(String videoId);
	List<CommentsVO> querySecondComments(@Param("videoId") String videoId,@Param("fid") String fid);
}