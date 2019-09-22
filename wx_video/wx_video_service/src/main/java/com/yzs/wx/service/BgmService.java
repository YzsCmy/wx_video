package com.yzs.wx.service;

import com.yzs.wx.pojo.Bgm;

import java.util.List;

public interface BgmService {
    List<Bgm> queryAll();

    String queryBgmPathById(String audioId);
}
