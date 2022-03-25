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
import com.iuaenasong.oj.mapper.MsgRemindMapper;
import com.iuaenasong.oj.pojo.entity.msg.MsgRemind;
import com.iuaenasong.oj.pojo.vo.UserMsgVo;
import com.iuaenasong.oj.pojo.vo.UserUnreadMsgCountVo;
import com.iuaenasong.oj.dao.msg.MsgRemindEntityService;

import javax.annotation.Resource;

@Service
public class MsgRemindEntityServiceImpl extends ServiceImpl<MsgRemindMapper, MsgRemind> implements MsgRemindEntityService {

    @Resource
    private MsgRemindMapper msgRemindMapper;
    @Override
    public UserUnreadMsgCountVo getUserUnreadMsgCount(String uid) {
        return msgRemindMapper.getUserUnreadMsgCount(uid);
    }

    @Override
    public IPage<UserMsgVo> getUserMsg(Page<UserMsgVo> page, String uid, String action) {
        return msgRemindMapper.getUserMsg(page, uid, action);
    }

}