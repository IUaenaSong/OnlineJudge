/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.discussion;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.pojo.entity.discussion.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iuaenasong.oj.pojo.entity.discussion.Reply;
import com.iuaenasong.oj.pojo.vo.CommentVo;

import java.util.List;

public interface CommentEntityService extends IService<Comment> {

    IPage<CommentVo> getCommentList(int limit, int currentPage, Long cid, Integer did, Boolean isRoot, String uid);

    void updateCommentMsg(String recipientId, String senderId, String content, Integer discussionId);

    void updateCommentLikeMsg(String recipientId, String senderId, Integer sourceId, String sourceType);
}
