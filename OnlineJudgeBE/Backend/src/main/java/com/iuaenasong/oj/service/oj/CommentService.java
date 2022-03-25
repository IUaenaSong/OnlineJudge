/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ReplyDto;
import com.iuaenasong.oj.pojo.entity.discussion.Comment;
import com.iuaenasong.oj.pojo.vo.CommentListVo;
import com.iuaenasong.oj.pojo.vo.CommentVo;
import com.iuaenasong.oj.pojo.vo.ReplyVo;

import java.util.List;

public interface CommentService {

    public CommonResult<CommentListVo> getComments(Long cid, Integer did, Integer limit, Integer currentPage);

    public CommonResult<CommentVo> addComment(Comment comment);

    public CommonResult<Void> deleteComment(Comment comment);

    public CommonResult<Void> addCommentLike(Integer cid, Boolean toLike, Integer sourceId, String sourceType);

    public CommonResult<List<ReplyVo>> getAllReply(Integer commentId, Long cid);

    public CommonResult<ReplyVo> addReply(ReplyDto replyDto);

    public CommonResult<Void> deleteReply(ReplyDto replyDto);
}
