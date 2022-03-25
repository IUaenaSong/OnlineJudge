/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.msg.UserMessageManager;
import com.iuaenasong.oj.pojo.vo.UserMsgVo;
import com.iuaenasong.oj.pojo.vo.UserUnreadMsgCountVo;
import com.iuaenasong.oj.service.msg.UserMessageService;

import javax.annotation.Resource;

@Service
public class UserMessageServiceImpl implements UserMessageService {

    @Resource
    private UserMessageManager userMessageManager;

    @Override
    public CommonResult<UserUnreadMsgCountVo> getUnreadMsgCount() {
        return CommonResult.successResponse(userMessageManager.getUnreadMsgCount());
    }

    @Override
    public CommonResult<Void> cleanMsg(String type, Long id) {
        try {
            userMessageManager.cleanMsg(type, id);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<IPage<UserMsgVo>> getCommentMsg(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(userMessageManager.getCommentMsg(limit, currentPage));
    }

    @Override
    public CommonResult<IPage<UserMsgVo>> getReplyMsg(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(userMessageManager.getReplyMsg(limit, currentPage));
    }

    @Override
    public CommonResult<IPage<UserMsgVo>> getLikeMsg(Integer limit, Integer currentPage) {
        return CommonResult.successResponse(userMessageManager.getLikeMsg(limit, currentPage));
    }
}