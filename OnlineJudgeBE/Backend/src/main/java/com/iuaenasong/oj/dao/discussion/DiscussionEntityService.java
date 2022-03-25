/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.discussion;

import com.baomidou.mybatisplus.extension.service.IService;

import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.vo.DiscussionVo;

public interface DiscussionEntityService extends IService<Discussion> {

    DiscussionVo getDiscussion(Integer did,String uid);

    void updatePostLikeMsg(String recipientId, String senderId, Integer discussionId);
}
