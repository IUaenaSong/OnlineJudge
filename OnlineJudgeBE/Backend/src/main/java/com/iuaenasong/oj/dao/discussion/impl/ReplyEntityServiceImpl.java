/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.discussion.impl;

import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.vo.ReplyVo;
import com.iuaenasong.oj.utils.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.mapper.ReplyMapper;
import com.iuaenasong.oj.pojo.entity.msg.MsgRemind;
import com.iuaenasong.oj.pojo.entity.discussion.Reply;
import com.iuaenasong.oj.dao.discussion.ReplyEntityService;
import com.iuaenasong.oj.dao.msg.MsgRemindEntityService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ReplyEntityServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements ReplyEntityService {

    @Resource
    private MsgRemindEntityService msgRemindEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Autowired
    private ReplyMapper replyMapper;

    @Override
    public List<ReplyVo> getAllReplyByCommentId(Long cid, String uid, Boolean isRoot, Integer commentId) {

        if (cid != null) {
            Contest contest = contestEntityService.getById(cid);
            boolean onlyMineAndAdmin = contest.getStatus().equals(Constants.Contest.STATUS_RUNNING.getCode())
                    && !isRoot && !contest.getUid().equals(uid);
            if (onlyMineAndAdmin) { // 自己和比赛管理者评论可看

                List<String> myAndAdminUidList = userInfoEntityService.getSuperAdminUidList();
                myAndAdminUidList.add(uid);
                myAndAdminUidList.add(contest.getUid());
                return replyMapper.getAllReplyByCommentId(commentId, myAndAdminUidList);
            }

        }
        return replyMapper.getAllReplyByCommentId(commentId, null);
    }

    @Async
    @Override
    public void updateReplyMsg(Integer sourceId, String sourceType, String content,
                               Integer quoteId, String quoteType, String recipientId, String senderId) {
        if (content.length() > 200) {
            content = content.substring(0, 200) + "...";
        }

        MsgRemind msgRemind = new MsgRemind();
        msgRemind.setAction("Reply")
                .setSourceId(sourceId)
                .setSourceType(sourceType)
                .setSourceContent(content)
                .setQuoteId(quoteId)
                .setQuoteType(quoteType)
                .setUrl(sourceType.equals("Discussion") ? "/discussion/" + sourceId : "/contest/" + sourceId + "/comment")
                .setRecipientId(recipientId)
                .setSenderId(senderId);

        msgRemindEntityService.saveOrUpdate(msgRemind);
    }
}