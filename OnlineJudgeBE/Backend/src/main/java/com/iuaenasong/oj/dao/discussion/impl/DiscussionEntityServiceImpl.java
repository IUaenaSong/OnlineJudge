/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.discussion.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.mapper.DiscussionMapper;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.entity.msg.MsgRemind;
import com.iuaenasong.oj.pojo.vo.DiscussionVo;
import com.iuaenasong.oj.dao.discussion.DiscussionEntityService;
import com.iuaenasong.oj.dao.msg.MsgRemindEntityService;

import javax.annotation.Resource;

@Service
public class DiscussionEntityServiceImpl extends ServiceImpl<DiscussionMapper, Discussion> implements DiscussionEntityService {

    @Autowired
    private DiscussionMapper discussionMapper;

    @Override
    public DiscussionVo getDiscussion(Integer did, String uid) {
        return discussionMapper.getDiscussion(did, uid);
    }

    @Resource
    private MsgRemindEntityService msgRemindEntityService;

    @Async
    public void updatePostLikeMsg(String recipientId, String senderId, Integer discussionId) {

        MsgRemind msgRemind = new MsgRemind();
        msgRemind.setAction("Like_Post")
                .setRecipientId(recipientId)
                .setSenderId(senderId)
                .setSourceId(discussionId)
                .setSourceType("Discussion")
                .setUrl("/discussion/" + discussionId);
        msgRemindEntityService.saveOrUpdate(msgRemind);
    }
}