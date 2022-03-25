/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.discussion;

import com.iuaenasong.oj.pojo.vo.ReplyVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.discussion.Reply;

import java.util.List;

public interface ReplyEntityService extends IService<Reply> {

    public List<ReplyVo> getAllReplyByCommentId(Long cid, String uid, Boolean isRoot, Integer commentId);

    public void updateReplyMsg(Integer sourceId, String sourceType, String content,
                               Integer quoteId, String quoteType, String recipientId,String senderId);
}