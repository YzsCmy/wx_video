package com.yzs.wx.service.impl;

import com.yzs.wx.mapper.BgmMapper;
import com.yzs.wx.pojo.Bgm;
import com.yzs.wx.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Override
    public List<Bgm> queryAll() {
        return bgmMapper.selectAll();
    }

    @Override
    public String queryBgmPathById(String audioId) {
        Bgm bgm = bgmMapper.selectByPrimaryKey(audioId);
        return bgm.getPath();
    }
}
