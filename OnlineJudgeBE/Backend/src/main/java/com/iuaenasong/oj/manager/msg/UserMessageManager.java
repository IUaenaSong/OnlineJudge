/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.msg;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.discussion.Comment;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.entity.discussion.Reply;
import com.iuaenasong.oj.pojo.entity.msg.MsgRemind;
import com.iuaenasong.oj.pojo.entity.msg.UserSysNotice;
import com.iuaenasong.oj.pojo.vo.UserMsgVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.pojo.vo.UserUnreadMsgCountVo;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.discussion.CommentEntityService;
import com.iuaenasong.oj.dao.discussion.DiscussionEntityService;
import com.iuaenasong.oj.dao.discussion.ReplyEntityService;
import com.iuaenasong.oj.dao.msg.MsgRemindEntityService;
import com.iuaenasong.oj.dao.msg.UserSysNoticeEntityService;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserMessageManager {

    @Resource
    private MsgRemindEntityService msgRemindEntityService;

    @Resource
    private ContestEntityService contestEntityService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private DiscussionEntityService discussionEntityService;

    @Resource
    private CommentEntityService commentEntityService;

    @Resource
    private ReplyEntityService replyEntityService;

    @Resource
    private UserSysNoticeEntityService userSysNoticeEntityService;

    public UserUnreadMsgCountVo getUnreadMsgCount() {
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        UserUnreadMsgCountVo userUnreadMsgCount = msgRemindEntityService.getUserUnreadMsgCount(userRolesVo.getUid());
        if (userUnreadMsgCount == null) {
            userUnreadMsgCount = new UserUnreadMsgCountVo(0, 0, 0, 0, 0);
        }
        return userUnreadMsgCount;
    }

    public void cleanMsg( String type, Long id) throws StatusFailException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        boolean isOk = cleanMsgByType(type, id, userRolesVo.getUid());
        if (!isOk) {
            throw new StatusFailException("清空失败");
        }
    }

    public IPage<UserMsgVo> getCommentMsg(Integer limit, Integer currentPage) {

        // 页数，每页题数若为空，设置默认值
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 5;
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        return getUserMsgList(userRolesVo.getUid(), "Discuss", limit, currentPage);
    }

    public IPage<UserMsgVo> getReplyMsg(Integer limit, Integer currentPage) {

        // 页数，每页题数若为空，设置默认值
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 5;

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        return getUserMsgList(userRolesVo.getUid(), "Reply", limit, currentPage);
    }

    public IPage<UserMsgVo> getLikeMsg(Integer limit, Integer currentPage) {

        // 页数，每页题数若为空，设置默认值
        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 5;

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        return getUserMsgList(userRolesVo.getUid(), "Like", limit, currentPage);
    }

    private boolean cleanMsgByType(String type, Long id, String uid) {

        switch (type) {
            case "Like":
            case "Discuss":
            case "Reply":
                UpdateWrapper<MsgRemind> updateWrapper1 = new UpdateWrapper<>();
                updateWrapper1
                        .eq(id != null, "id", id)
                        .eq("recipient_id", uid);
                return msgRemindEntityService.remove(updateWrapper1);
            case "Sys":
            case "Mine":
                UpdateWrapper<UserSysNotice> updateWrapper2 = new UpdateWrapper<>();
                updateWrapper2
                        .eq(id != null, "id", id)
                        .eq("recipient_id", uid);
                return userSysNoticeEntityService.remove(updateWrapper2);
        }
        return false;
    }

    private IPage<UserMsgVo> getUserMsgList(String uid, String action, int limit, int currentPage) {
        Page<UserMsgVo> page = new Page<>(currentPage, limit);
        IPage<UserMsgVo> userMsgList = msgRemindEntityService.getUserMsg(page, uid, action);
        if (userMsgList.getTotal() > 0) {
            switch (action) {
                case "Discuss":  // 评论我的
                    return getUserDiscussMsgList(userMsgList);
                case "Reply": // 回复我的
                    return getUserReplyMsgList(userMsgList);
                case "Like":
                    return getUserLikeMsgList(userMsgList);
                default:
                    throw new RuntimeException("invalid action:" + action);
            }
        } else {
            return userMsgList;
        }
    }

    private IPage<UserMsgVo> getUserDiscussMsgList(IPage<UserMsgVo> userMsgList) {

        List<Integer> discussionIds = userMsgList.getRecords()
                .stream()
                .map(UserMsgVo::getSourceId)
                .collect(Collectors.toList());
        Collection<Discussion> discussions = discussionEntityService.listByIds(discussionIds);
        for (Discussion discussion : discussions) {
            for (UserMsgVo userMsgVo : userMsgList.getRecords()) {
                if (Objects.equals(discussion.getId(), userMsgVo.getSourceId())) {
                    userMsgVo.setSourceTitle(discussion.getTitle());
                    break;
                }
            }
        }
        applicationContext.getBean(UserMessageManager.class).updateUserMsgRead(userMsgList);
        return userMsgList;
    }

    private IPage<UserMsgVo> getUserReplyMsgList(IPage<UserMsgVo> userMsgList) {

        for (UserMsgVo userMsgVo : userMsgList.getRecords()) {
            if ("Discussion".equals(userMsgVo.getSourceType())) {
                Discussion discussion = discussionEntityService.getById(userMsgVo.getSourceId());
                if (discussion != null) {
                    userMsgVo.setSourceTitle(discussion.getTitle());
                } else {
                    userMsgVo.setSourceTitle("原讨论帖已被删除!【The original discussion post has been deleted!】");
                }
            } else if ("Contest".equals(userMsgVo.getSourceType())) {
                Contest contest = contestEntityService.getById(userMsgVo.getSourceId());
                if (contest != null) {
                    userMsgVo.setSourceTitle(contest.getTitle());
                } else {
                    userMsgVo.setSourceTitle("原比赛已被删除!【The original contest has been deleted!】");
                }
            }

            if ("Comment".equals(userMsgVo.getQuoteType())) {
                Comment comment = commentEntityService.getById(userMsgVo.getQuoteId());
                if (comment != null) {
                    String content;
                    if (comment.getContent().length() < 100) {
                        content = comment.getFromName() + " : "
                                + comment.getContent();

                    } else {
                        content = comment.getFromName() + " : "
                                + comment.getContent().substring(0, 100) + "...";
                    }
                    userMsgVo.setQuoteContent(content);
                } else {
                    userMsgVo.setQuoteContent("您的原评论信息已被删除！【Your original comments have been deleted!】");
                }

            } else if ("Reply".equals(userMsgVo.getQuoteType())) {
                Reply reply = replyEntityService.getById(userMsgVo.getQuoteId());
                if (reply != null) {
                    String content;
                    if (reply.getContent().length() < 100) {
                        content = reply.getFromName() + " : @" + reply.getToName() + "："
                                + reply.getContent();

                    } else {
                        content = reply.getFromName() + " : @" + reply.getToName() + "："
                                + reply.getContent().substring(0, 100) + "...";
                    }
                    userMsgVo.setQuoteContent(content);
                } else {
                    userMsgVo.setQuoteContent("您的原回复信息已被删除！【Your original reply has been deleted!】");
                }
            }

        }

        applicationContext.getBean(UserMessageManager.class).updateUserMsgRead(userMsgList);
        return userMsgList;
    }

    private IPage<UserMsgVo> getUserLikeMsgList(IPage<UserMsgVo> userMsgList) {
        for (UserMsgVo userMsgVo : userMsgList.getRecords()) {
            if ("Discussion".equals(userMsgVo.getSourceType())) {
                Discussion discussion = discussionEntityService.getById(userMsgVo.getSourceId());
                if (discussion != null) {
                    userMsgVo.setSourceTitle(discussion.getTitle());
                } else {
                    userMsgVo.setSourceTitle("原讨论帖已被删除!【The original discussion post has been deleted!】");
                }
            } else if ("Contest".equals(userMsgVo.getSourceType())) {
                Contest contest = contestEntityService.getById(userMsgVo.getSourceId());
                if (contest != null) {
                    userMsgVo.setSourceTitle(contest.getTitle());
                } else {
                    userMsgVo.setSourceTitle("原比赛已被删除!【The original contest has been deleted!】");
                }
            }
        }
        applicationContext.getBean(UserMessageManager.class).updateUserMsgRead(userMsgList);
        return userMsgList;
    }

    @Async
    public void updateUserMsgRead(IPage<UserMsgVo> userMsgList) {
        List<Long> idList = userMsgList.getRecords().stream()
                .filter(userMsgVo -> !userMsgVo.getState())
                .map(UserMsgVo::getId)
                .collect(Collectors.toList());
        if (idList.size() == 0) {
            return;
        }
        UpdateWrapper<MsgRemind> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idList)
                .set("state", true);
        msgRemindEntityService.update(null, updateWrapper);
    }

}