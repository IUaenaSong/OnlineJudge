/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import com.iuaenasong.oj.pojo.vo.ACMRankVo;
import com.iuaenasong.oj.pojo.entity.user.UserRecord;
import com.iuaenasong.oj.mapper.UserRecordMapper;
import com.iuaenasong.oj.pojo.vo.OIRankVo;
import com.iuaenasong.oj.pojo.vo.UserHomeVo;
import com.iuaenasong.oj.dao.user.UserRecordEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRecordEntityServiceImpl extends ServiceImpl<UserRecordMapper, UserRecord> implements UserRecordEntityService {

    @Autowired
    private UserRecordMapper userRecordMapper;

    @Override
    public List<ACMRankVo> getRecent7ACRank() {
        return userRecordMapper.getRecent7ACRank();
    }

    @Override
    public UserHomeVo getUserHomeInfo(String uid, String username) {
        return userRecordMapper.getUserHomeInfo(uid, username);
    }

    @Override
    public IPage<OIRankVo> getOIRankList(Page<OIRankVo> page, List<String> uidList) {
        return userRecordMapper.getOIRankList(page, uidList);
    }

    @Override
    public IPage<ACMRankVo> getACMRankList(Page<ACMRankVo> page, List<String> uidList) {
        return userRecordMapper.getACMRankList(page, uidList);
    }

}
