package com.yzs.wx.mapper;

import com.yzs.wx.utils.MyMapper;
import com.yzs.wx.pojo.Videos;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface VideosMapper extends MyMapper<Videos>{

    @Update("update videos set cover_path=#{path} where id=#{id}")
    void updateCoverPathById(@Param("id")String id,@Param("path") String path);
}