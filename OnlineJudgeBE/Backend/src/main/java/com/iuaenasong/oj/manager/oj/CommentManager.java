/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.emoji.EmojiUtil;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.contest.ContestRegisterEntityService;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestRegister;
import com.iuaenasong.oj.pojo.vo.ReplyVo;
import com.iuaenasong.oj.validator.ContestValidator;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.pojo.dto.ReplyDto;
import com.iuaenasong.oj.pojo.entity.discussion.Comment;
import com.iuaenasong.oj.pojo.entity.discussion.CommentLike;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.entity.discussion.Reply;
import com.iuaenasong.oj.pojo.entity.user.UserAcproblem;
import com.iuaenasong.oj.pojo.vo.CommentListVo;
import com.iuaenasong.oj.pojo.vo.CommentVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.dao.discussion.CommentEntityService;
import com.iuaenasong.oj.dao.discussion.CommentLikeEntityService;
import com.iuaenasong.oj.dao.discussion.DiscussionEntityService;
import com.iuaenasong.oj.dao.discussion.ReplyEntityService;
import com.iuaenasong.oj.dao.user.UserAcproblemEntityService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class CommentManager {

    @Autowired
    private CommentEntityService commentEntityService;

    @Autowired
    private CommentLikeEntityService commentLikeEntityService;

    @Autowired
    private ReplyEntityService replyEntityService;

    @Autowired
    private DiscussionEntityService discussionEntityService;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private ContestEntityService contestEntityService;

    @Autowired
    private ContestRegisterEntityService contestRegisterEntityService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private ContestValidator contestValidator;

    public CommentListVo getComments(Long cid, Integer did, Integer limit, Integer currentPage) throws StatusForbiddenException {

        // 如果有登录，则获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
        discussionQueryWrapper.select("id", "gid").eq("id", did);
        Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);
        Long gid = null;
        if (discussion != null) {
            gid = discussion.getGid();
        }
        if (gid != null) {
            if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        IPage<CommentVo> commentList = commentEntityService.getCommentList(limit, currentPage, cid, did, isRoot,
                userRolesVo != null ? userRolesVo.getUid() : null);

        HashMap<Integer, Boolean> commentLikeMap = new HashMap<>();

        if (userRolesVo != null) {
            // 如果是有登录 需要检查是否对评论有点赞
            List<Integer> commentIdList = new LinkedList<>();

            for (CommentVo commentVo : commentList.getRecords()) {
                commentIdList.add(commentVo.getId());
            }

            if (commentIdList.size() > 0) {

                QueryWrapper<CommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
                commentLikeQueryWrapper.in("cid", commentIdList).eq("uid", userRolesVo.getUid());

                List<CommentLike> commentLikeList = commentLikeEntityService.list(commentLikeQueryWrapper);

                // 如果存在记录需要修正Map为true
                for (CommentLike tmp : commentLikeList) {
                    commentLikeMap.put(tmp.getCid(), true);
                }
            }
        }

        CommentListVo commentListVo = new CommentListVo();
        commentListVo.setCommentList(commentList);
        commentListVo.setCommentLikeMap(commentLikeMap);
        return commentListVo;
    }

    @Transactional
    public CommentVo addComment(Comment comment) throws StatusFailException, StatusForbiddenException {

        if (StringUtils.isEmpty(comment.getContent().trim())) {
            throw new StatusFailException("评论内容不能为空！");
        }

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Long cid = comment.getCid();

        // 比赛外的评论 除管理员外 只有AC 10道以上才可评论
        if (cid == null) {
            if (!isRoot && !isProblemAdmin && !isAdmin) {
                QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
                int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);

                if (userAcProblemCount < 10 && userRolesVo.getMobile() == null) {
                    throw new StatusForbiddenException("对不起，您暂时不能评论！请先去绑定手机号或提交题目通过10道以上!");
                }
            }

            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);

            Long gid = discussion.getGid();
            if (gid != null) {
                if (!isRoot && !discussion.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("对不起，您无权限操作！");
                }
            }
        } else {
            Contest contest = contestEntityService.getById(cid);
            contestValidator.validateContestAuth(contest, userRolesVo, isRoot);
        }

        comment.setFromAvatar(userRolesVo.getAvatar())
                .setFromName(userRolesVo.getUsername())
                .setFromUid(userRolesVo.getUid());

        if (SecurityUtils.getSubject().hasRole("root")) {
            comment.setFromRole("root");
        } else if (SecurityUtils.getSubject().hasRole("admin")
                || SecurityUtils.getSubject().hasRole("problem_admin")) {
            comment.setFromRole("admin");
        } else {
            comment.setFromRole("user");
        }

        // 带有表情的字符串转换为编码
        comment.setContent(EmojiUtil.toHtml(comment.getContent()));

        boolean isOk = commentEntityService.saveOrUpdate(comment);

        if (isOk) {
            CommentVo commentVo = new CommentVo();
            commentVo.setContent(comment.getContent());
            commentVo.setId(comment.getId());
            commentVo.setFromAvatar(comment.getFromAvatar());
            commentVo.setFromName(comment.getFromName());
            commentVo.setFromUid(comment.getFromUid());
            commentVo.setLikeNum(0);
            commentVo.setGmtCreate(comment.getGmtCreate());
            commentVo.setReplyList(new LinkedList<>());
            commentVo.setFromTitleName(userRolesVo.getTitleName());
            commentVo.setFromTitleColor(userRolesVo.getTitleColor());
            // 如果是讨论区的回复，发布成功需要添加统计该讨论的回复数
            if (comment.getDid() != null) {
                Discussion discussion = discussionEntityService.getById(comment.getDid());
                if (discussion != null) {
                    discussion.setCommentNum(discussion.getCommentNum() + 1);
                    discussionEntityService.updateById(discussion);
                    // 更新消息
                    commentEntityService.updateCommentMsg(discussion.getUid(),
                            userRolesVo.getUid(),
                            comment.getContent(),
                            comment.getDid());
                }
            }
            return commentVo;
        } else {
            throw new StatusFailException("评论失败，请重新尝试！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Comment comment) throws StatusForbiddenException, StatusFailException {
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");
        // 如果不是评论本人 或者不是管理员 无权限删除该评论

        Long cid = comment.getCid();

        if (cid == null) {
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);

            Long gid = discussion.getGid();
            if (gid == null) {
                if (!comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !isProblemAdmin && !isAdmin) {
                    throw new StatusForbiddenException("无权删除该评论");
                }
            } else {
                if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot) {
                    throw new StatusForbiddenException("无权删除该评论");
                }
            }
        } else {
            Contest contest = contestEntityService.getById(cid);

            Long gid = contest.getGid();
            if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !contest.getUid().equals(userRolesVo.getUid())) {
                throw new StatusForbiddenException("无权删除该评论");
            }
        }
        // 获取需要删除该评论的回复数
        int replyNum = replyEntityService.count(new QueryWrapper<Reply>().eq("comment_id", comment.getId()));

        // 删除该数据 包括关联外键的reply表数据
        boolean isDeleteComment = commentEntityService.removeById(comment.getId());

        // 同时需要删除该评论的回复表数据
        replyEntityService.remove(new UpdateWrapper<Reply>().eq("comment_id", comment.getId()));

        if (isDeleteComment) {
            // 如果是讨论区的回复，删除成功需要减少统计该讨论的回复数
            if (comment.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", comment.getDid())
                        .setSql("comment_num=comment_num-" + (replyNum + 1));
                discussionEntityService.update(discussionUpdateWrapper);
            }
        } else {
            throw new StatusFailException("删除失败，请重新尝试");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addCommentLike(Integer cid, Boolean toLike, Integer sourceId, String sourceType) throws StatusFailException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        QueryWrapper<CommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
        commentLikeQueryWrapper.eq("cid", cid).eq("uid", userRolesVo.getUid());

        CommentLike commentLike = commentLikeEntityService.getOne(commentLikeQueryWrapper, false);

        if (toLike) { // 添加点赞
            if (commentLike == null) { // 如果不存在就添加
                boolean isSave = commentLikeEntityService.saveOrUpdate(new CommentLike()
                        .setUid(userRolesVo.getUid())
                        .setCid(cid));
                if (!isSave) {
                    throw new StatusFailException("点赞失败，请重试尝试！");
                }
            }
            // 点赞+1
            Comment comment = commentEntityService.getById(cid);
            if (comment != null) {
                comment.setLikeNum(comment.getLikeNum() + 1);
                commentEntityService.updateById(comment);
                // 当前的评论要不是点赞者的 才发送点赞消息
                if (!userRolesVo.getUsername().equals(comment.getFromName())) {
                    commentEntityService.updateCommentLikeMsg(comment.getFromUid(), userRolesVo.getUid(), sourceId, sourceType);
                }
            }
        } else { // 取消点赞
            if (commentLike != null) { // 如果存在就删除
                boolean isDelete = commentLikeEntityService.removeById(commentLike.getId());
                if (!isDelete) {
                    throw new StatusFailException("取消点赞失败，请重试尝试！");
                }
            }
            // 点赞-1
            UpdateWrapper<Comment> commentUpdateWrapper = new UpdateWrapper<>();
            commentUpdateWrapper.setSql("like_num=like_num-1").eq("id", cid);
            commentEntityService.update(commentUpdateWrapper);
        }

    }

    public List<ReplyVo> getAllReply(Integer commentId, Long cid) throws StatusForbiddenException, StatusFailException {

        // 如果有登录，则获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");
        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Comment comment = commentEntityService.getById(commentId);

        if (cid == null) {
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());

            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);
            Long gid = discussion.getGid();
            if (gid != null) {
                if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("对不起，您无权限操作！");
                }
            }
        } else {
            Contest contest = contestEntityService.getById(cid);
            contestValidator.validateContestAuth(contest, userRolesVo, isRoot);
            Long gid = contest.getGid();
            if (!comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !contest.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }
        return replyEntityService.getAllReplyByCommentId(cid,
                userRolesVo != null ? userRolesVo.getUid() : null,
                isRoot,
                commentId);
    }

    public ReplyVo addReply(ReplyDto replyDto) throws StatusFailException, StatusForbiddenException {

        if (StringUtils.isEmpty(replyDto.getReply().getContent().trim())) {
            throw new StatusFailException("回复内容不能为空！");
        }

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Reply reply = replyDto.getReply();

        Comment comment = commentEntityService.getById(reply.getCommentId());

        Long cid = comment.getCid();

        if (cid == null) {
            if (!isRoot && !isProblemAdmin && !isAdmin) {
                QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
                int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);

                if (userAcProblemCount < 10 && userRolesVo.getMobile() == null) {
                    throw new StatusForbiddenException("对不起，您暂时不能回复！请先去绑定手机号或提交题目通过10道以上!");
                }
            }
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);

            Long gid = discussion.getGid();
            if (gid != null) {
                if (!isRoot && !comment.getFromUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("对不起，您无权限操作！");
                }
            }
        } else {
            Contest contest = contestEntityService.getById(cid);
            contestValidator.validateContestAuth(contest, userRolesVo, isRoot);
            Long gid = contest.getGid();
            if (!comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !contest.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }
        reply.setFromAvatar(userRolesVo.getAvatar())
                .setFromName(userRolesVo.getUsername())
                .setFromUid(userRolesVo.getUid());

        if (SecurityUtils.getSubject().hasRole("root")) {
            reply.setFromRole("root");
        } else if (SecurityUtils.getSubject().hasRole("admin")
                || SecurityUtils.getSubject().hasRole("problem_admin")) {
            reply.setFromRole("admin");
        } else {
            reply.setFromRole("user");
        }
        // 带有表情的字符串转换为编码
        reply.setContent(EmojiUtil.toHtml(reply.getContent()));

        boolean isOk = replyEntityService.saveOrUpdate(reply);

        if (isOk) {
            // 如果是讨论区的回复，发布成功需要增加统计该讨论的回复数
            if (replyDto.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", replyDto.getDid())
                        .setSql("comment_num=comment_num+1");
                discussionEntityService.update(discussionUpdateWrapper);
                // 更新消息
                replyEntityService.updateReplyMsg(replyDto.getDid(),
                        "Discussion",
                        reply.getContent(),
                        replyDto.getQuoteId(),
                        replyDto.getQuoteType(),
                        reply.getToUid(),
                        reply.getFromUid());
            }

            ReplyVo replyVo = new ReplyVo();
            BeanUtil.copyProperties(reply, replyVo);
            replyVo.setFromTitleName(userRolesVo.getTitleName());
            replyVo.setFromTitleColor(userRolesVo.getTitleColor());
            return replyVo;
        } else {
            throw new StatusFailException("回复失败，请重新尝试！");
        }
    }

    public void deleteReply(ReplyDto replyDto) throws StatusForbiddenException, StatusFailException {
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Reply reply = replyDto.getReply();

        Comment comment = commentEntityService.getById(reply.getCommentId());

        Long cid = comment.getCid();

        if (cid == null) {
            Discussion discussion = discussionEntityService.getById(comment.getDid());

            Long gid = discussion.getGid();
            if (gid == null) {
                if (!reply.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !isProblemAdmin && !isAdmin) {
                    throw new StatusForbiddenException("无权删除该回复");
                }
            } else {
                if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !reply.getFromUid().equals(userRolesVo.getUid()) && !isRoot) {
                    throw new StatusForbiddenException("无权删除该回复");
                }
            }
        } else {
            Contest contest = contestEntityService.getById(cid);

            Long gid = contest.getGid();
            if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !reply.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !contest.getUid().equals(userRolesVo.getUid())) {
                throw new StatusForbiddenException("无权删除该回复");
            }
        }

        boolean isOk = replyEntityService.removeById(reply.getId());
        if (isOk) {
            // 如果是讨论区的回复，删除成功需要减少统计该讨论的回复数
            if (replyDto.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", replyDto.getDid())
                        .setSql("comment_num=comment_num-1");
                discussionEntityService.update(discussionUpdateWrapper);
            }
        } else {
            throw new StatusFailException("删除失败，请重新尝试");
        }
    }
}