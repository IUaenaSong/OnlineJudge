/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.emoji.EmojiUtil;
import com.iuaenasong.oj.annotation.OJAccessEnum;
import com.iuaenasong.oj.dao.contest.ContestEntityService;
import com.iuaenasong.oj.dao.contest.ContestRegisterEntityService;
import com.iuaenasong.oj.exception.AccessException;
import com.iuaenasong.oj.pojo.entity.contest.Contest;
import com.iuaenasong.oj.pojo.entity.contest.ContestRegister;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.validator.AccessValidator;
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

    @Autowired
    private AccessValidator accessValidator;

    @Autowired
    private ConfigVo configVo;

    public CommentListVo getComments(Long cid, Integer did, Integer limit, Integer currentPage) throws StatusForbiddenException, AccessException {

        // ????????????????????????????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        if (cid == null && did != null) {
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", did);
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);
            if (discussion != null && discussion.getGid() != null) {
                accessValidator.validateAccess(OJAccessEnum.GROUP_DISCUSSION);
                if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), discussion.getGid())) {
                    throw new StatusForbiddenException("?????????????????????????????????");
                }
            } else {
                accessValidator.validateAccess(OJAccessEnum.PUBLIC_DISCUSSION);
            }
        } else {
            accessValidator.validateAccess(OJAccessEnum.CONTEST_COMMENT);
        }

        IPage<CommentVo> commentList = commentEntityService.getCommentList(limit, currentPage, cid, did, isRoot,
                userRolesVo != null ? userRolesVo.getUid() : null);

        HashMap<Integer, Boolean> commentLikeMap = new HashMap<>();

        if (userRolesVo != null) {
            // ?????????????????? ????????????????????????????????????
            List<Integer> commentIdList = new LinkedList<>();

            for (CommentVo commentVo : commentList.getRecords()) {
                commentIdList.add(commentVo.getId());
            }

            if (commentIdList.size() > 0) {

                QueryWrapper<CommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
                commentLikeQueryWrapper.in("cid", commentIdList).eq("uid", userRolesVo.getUid());

                List<CommentLike> commentLikeList = commentLikeEntityService.list(commentLikeQueryWrapper);

                // ??????????????????????????????Map???true
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
    public CommentVo addComment(Comment comment) throws StatusFailException, StatusForbiddenException, AccessException  {

        if (StringUtils.isEmpty(comment.getContent().trim())) {
            throw new StatusFailException("???????????????????????????");
        }

        // ???????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Long cid = comment.getCid();

        // ?????????????????? ??????????????? ??????AC 10?????????????????????
        if (cid == null) {

            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);

            Long gid = discussion.getGid();
            if (gid != null) {
                accessValidator.validateAccess(OJAccessEnum.GROUP_DISCUSSION);
                if (!isRoot && !discussion.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("?????????????????????????????????");
                }
            } else {
                accessValidator.validateAccess(OJAccessEnum.PUBLIC_DISCUSSION);
            }

            if (!isRoot && !isProblemAdmin && !isAdmin) {
                QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
                int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);

                if (userAcProblemCount < configVo.getDefaultCreateCommentACInitValue() && userRolesVo.getMobile() == null) {
                    throw new StatusForbiddenException("?????????????????????????????????????????????????????????????????????????????????"
                            + configVo.getDefaultCreateCommentACInitValue() + "?????????!");
                }
            }
        } else {
            accessValidator.validateAccess(OJAccessEnum.CONTEST_COMMENT);
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

        // ???????????????????????????????????????
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
            // ?????????????????????????????????????????????????????????????????????????????????
            if (comment.getDid() != null) {
                Discussion discussion = discussionEntityService.getById(comment.getDid());
                if (discussion != null) {
                    discussion.setCommentNum(discussion.getCommentNum() + 1);
                    discussionEntityService.updateById(discussion);
                    // ????????????
                    commentEntityService.updateCommentMsg(discussion.getUid(),
                            userRolesVo.getUid(),
                            comment.getContent(),
                            comment.getDid());
                }
            }
            return commentVo;
        } else {
            throw new StatusFailException("?????????????????????????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Comment comment) throws StatusForbiddenException, StatusFailException, AccessException  {
        // ???????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");
        // ???????????????????????? ????????????????????? ????????????????????????

        Long cid = comment.getCid();

        if (cid == null) {
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);

            Long gid = discussion.getGid();
            if (gid == null) {
                accessValidator.validateAccess(OJAccessEnum.PUBLIC_DISCUSSION);
                if (!comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !isProblemAdmin && !isAdmin) {
                    throw new StatusForbiddenException("?????????????????????");
                }
            } else {
                accessValidator.validateAccess(OJAccessEnum.GROUP_DISCUSSION);
                if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot) {
                    throw new StatusForbiddenException("?????????????????????");
                }
            }
        } else {
            accessValidator.validateAccess(OJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);

            Long gid = contest.getGid();
            if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !contest.getUid().equals(userRolesVo.getUid())) {
                throw new StatusForbiddenException("?????????????????????");
            }
        }
        // ???????????????????????????????????????
        int replyNum = replyEntityService.count(new QueryWrapper<Reply>().eq("comment_id", comment.getId()));

        // ??????????????? ?????????????????????reply?????????
        boolean isDeleteComment = commentEntityService.removeById(comment.getId());

        // ?????????????????????????????????????????????
        replyEntityService.remove(new UpdateWrapper<Reply>().eq("comment_id", comment.getId()));

        if (isDeleteComment) {
            // ?????????????????????????????????????????????????????????????????????????????????
            if (comment.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", comment.getDid())
                        .setSql("comment_num=comment_num-" + (replyNum + 1));
                discussionEntityService.update(discussionUpdateWrapper);
            }
        } else {
            throw new StatusFailException("??????????????????????????????");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addCommentLike(Integer cid, Boolean toLike, Integer sourceId, String sourceType) throws StatusFailException {

        // ???????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        QueryWrapper<CommentLike> commentLikeQueryWrapper = new QueryWrapper<>();
        commentLikeQueryWrapper.eq("cid", cid).eq("uid", userRolesVo.getUid());

        CommentLike commentLike = commentLikeEntityService.getOne(commentLikeQueryWrapper, false);

        if (toLike) { // ????????????
            if (commentLike == null) { // ????????????????????????
                boolean isSave = commentLikeEntityService.saveOrUpdate(new CommentLike()
                        .setUid(userRolesVo.getUid())
                        .setCid(cid));
                if (!isSave) {
                    throw new StatusFailException("?????????????????????????????????");
                }
            }
            // ??????+1
            Comment comment = commentEntityService.getById(cid);
            if (comment != null) {
                comment.setLikeNum(comment.getLikeNum() + 1);
                commentEntityService.updateById(comment);
                // ???????????????????????????????????? ?????????????????????
                if (!userRolesVo.getUsername().equals(comment.getFromName())) {
                    commentEntityService.updateCommentLikeMsg(comment.getFromUid(), userRolesVo.getUid(), sourceId, sourceType);
                }
            }
        } else { // ????????????
            if (commentLike != null) { // ?????????????????????
                boolean isDelete = commentLikeEntityService.removeById(commentLike.getId());
                if (!isDelete) {
                    throw new StatusFailException("???????????????????????????????????????");
                }
            }
            // ??????-1
            UpdateWrapper<Comment> commentUpdateWrapper = new UpdateWrapper<>();
            commentUpdateWrapper.setSql("like_num=like_num-1").eq("id", cid);
            commentEntityService.update(commentUpdateWrapper);
        }

    }

    public List<ReplyVo> getAllReply(Integer commentId, Long cid) throws StatusForbiddenException, StatusFailException, AccessException  {

        // ????????????????????????????????????????????????
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
                accessValidator.validateAccess(OJAccessEnum.GROUP_DISCUSSION);
                if (!isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("?????????????????????????????????");
                }
            } else {
                accessValidator.validateAccess(OJAccessEnum.PUBLIC_DISCUSSION);
            }
        } else {
            accessValidator.validateAccess(OJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);
            contestValidator.validateContestAuth(contest, userRolesVo, isRoot);
            Long gid = contest.getGid();
            if (!comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !contest.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("?????????????????????????????????");
            }
        }
        return replyEntityService.getAllReplyByCommentId(cid,
                userRolesVo != null ? userRolesVo.getUid() : null,
                isRoot,
                commentId);
    }

    public ReplyVo addReply(ReplyDto replyDto) throws StatusFailException, StatusForbiddenException, AccessException  {

        if (StringUtils.isEmpty(replyDto.getReply().getContent().trim())) {
            throw new StatusFailException("???????????????????????????");
        }

        // ???????????????????????????
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        Reply reply = replyDto.getReply();

        Comment comment = commentEntityService.getById(reply.getCommentId());

        Long cid = comment.getCid();

        if (cid == null) {
            QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();
            discussionQueryWrapper.select("id", "gid").eq("id", comment.getDid());
            Discussion discussion = discussionEntityService.getOne(discussionQueryWrapper);

            Long gid = discussion.getGid();
            if (gid != null) {
                accessValidator.validateAccess(OJAccessEnum.GROUP_DISCUSSION);
                if (!isRoot && !comment.getFromUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                    throw new StatusForbiddenException("?????????????????????????????????");
                }
            } else {
                accessValidator.validateAccess(OJAccessEnum.PUBLIC_DISCUSSION);
            }

            if (!isRoot && !isProblemAdmin && !isAdmin) {
                QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
                int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);

                if (userAcProblemCount < configVo.getDefaultCreateCommentACInitValue() && userRolesVo.getMobile() == null) {
                    throw new StatusForbiddenException("?????????????????????????????????????????????????????????????????????????????????" +
                            configVo.getDefaultCreateCommentACInitValue() + "?????????!");
                }
            }
        } else {
            accessValidator.validateAccess(OJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);
            contestValidator.validateContestAuth(contest, userRolesVo, isRoot);
            Long gid = contest.getGid();
            if (!comment.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !contest.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("?????????????????????????????????");
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
        // ???????????????????????????????????????
        reply.setContent(EmojiUtil.toHtml(reply.getContent()));

        boolean isOk = replyEntityService.saveOrUpdate(reply);

        if (isOk) {
            // ?????????????????????????????????????????????????????????????????????????????????
            if (replyDto.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", replyDto.getDid())
                        .setSql("comment_num=comment_num+1");
                discussionEntityService.update(discussionUpdateWrapper);
                // ????????????
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
            throw new StatusFailException("?????????????????????????????????");
        }
    }

    public void deleteReply(ReplyDto replyDto) throws StatusForbiddenException, StatusFailException, AccessException  {
        // ???????????????????????????
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
                accessValidator.validateAccess(OJAccessEnum.PUBLIC_DISCUSSION);
                if (!reply.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !isProblemAdmin && !isAdmin) {
                    throw new StatusForbiddenException("?????????????????????");
                }
            } else {
                accessValidator.validateAccess(OJAccessEnum.GROUP_DISCUSSION);
                if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !reply.getFromUid().equals(userRolesVo.getUid()) && !isRoot) {
                    throw new StatusForbiddenException("?????????????????????");
                }
            }
        } else {
            accessValidator.validateAccess(OJAccessEnum.CONTEST_COMMENT);
            Contest contest = contestEntityService.getById(cid);

            Long gid = contest.getGid();
            if (!groupValidator.isGroupRoot(userRolesVo.getUid(), gid) && !reply.getFromUid().equals(userRolesVo.getUid()) && !isRoot && !contest.getUid().equals(userRolesVo.getUid())) {
                throw new StatusForbiddenException("?????????????????????");
            }
        }

        boolean isOk = replyEntityService.removeById(reply.getId());
        if (isOk) {
            // ?????????????????????????????????????????????????????????????????????????????????
            if (replyDto.getDid() != null) {
                UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
                discussionUpdateWrapper.eq("id", replyDto.getDid())
                        .setSql("comment_num=comment_num-1");
                discussionEntityService.update(discussionUpdateWrapper);
            }
        } else {
            throw new StatusFailException("??????????????????????????????");
        }
    }
}