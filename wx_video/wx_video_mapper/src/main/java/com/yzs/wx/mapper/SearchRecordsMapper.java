package com.yzs.wx.mapper;

import com.yzs.wx.utils.MyMapper;
import com.yzs.wx.pojo.SearchRecords;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    List<String> getHotwords();
}