/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.mapper.UserSysNoticeMapper;
import com.iuaenasong.oj.pojo.entity.msg.UserSysNotice;
import com.iuaenasong.oj.pojo.vo.SysMsgVo;
import com.iuaenasong.oj.dao.msg.UserSysNoticeEntityService;

import javax.annotation.Resource;

@Service
public class UserSysNoticeEntityServiceImpl extends ServiceImpl<UserSysNoticeMapper, UserSysNotice> implements UserSysNoticeEntityService {

    @Resource
    private UserSysNoticeMapper userSysNoticeMapper;

    @Override
    public IPage<SysMsgVo> getSysNotice(int limit, int currentPage, String uid) {
        Page<SysMsgVo> page = new Page<>(currentPage, limit);
        return userSysNoticeMapper.getSysOrMineNotice(page, uid, "Sys");
    }

    @Override
    public IPage<SysMsgVo> getMineNotice(int limit, int currentPage, String uid) {
        Page<SysMsgVo> page = new Page<>(currentPage, limit);
        return userSysNoticeMapper.getSysOrMineNotice(page, uid, "Mine");
    }

}