/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.oj;

import com.iuaenasong.oj.dao.problem.ProblemEntityService;
import com.iuaenasong.oj.pojo.entity.problem.Problem;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.pojo.entity.discussion.Discussion;
import com.iuaenasong.oj.pojo.entity.discussion.DiscussionLike;
import com.iuaenasong.oj.pojo.entity.discussion.DiscussionReport;
import com.iuaenasong.oj.pojo.entity.problem.Category;
import com.iuaenasong.oj.pojo.entity.user.UserAcproblem;
import com.iuaenasong.oj.pojo.vo.DiscussionVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.dao.discussion.DiscussionEntityService;
import com.iuaenasong.oj.dao.discussion.DiscussionLikeEntityService;
import com.iuaenasong.oj.dao.discussion.DiscussionReportEntityService;
import com.iuaenasong.oj.dao.problem.CategoryEntityService;
import com.iuaenasong.oj.dao.user.UserAcproblemEntityService;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.utils.RedisUtils;

import java.util.List;

@Component
public class DiscussionManager {
    @Autowired
    private DiscussionEntityService discussionEntityService;

    @Autowired
    private DiscussionLikeEntityService discussionLikeEntityService;

    @Autowired
    private CategoryEntityService categoryEntityService;

    @Autowired
    private DiscussionReportEntityService discussionReportEntityService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private ProblemEntityService problemEntityService;

    @Autowired
    private GroupValidator groupValidator;

    public IPage<Discussion> getDiscussionList(Integer limit,
                                               Integer currentPage,
                                               Integer categoryId,
                                               String pid,
                                               Boolean onlyMine,
                                               String keyword,
                                               Boolean admin) throws StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        QueryWrapper<Discussion> discussionQueryWrapper = new QueryWrapper<>();

        IPage<Discussion> iPage = new Page<>(currentPage, limit);

        if (categoryId != null) {
            discussionQueryWrapper.eq("category_id", categoryId);
        }

        if (!StringUtils.isEmpty(keyword)) {

            final String key = keyword.trim();

            discussionQueryWrapper.and(wrapper -> wrapper.like("title", key).or()
                    .like("author", key).or()
                    .like("id", key).or()
                    .like("description", key));
        }

        boolean isAdmin = SecurityUtils.getSubject().hasRole("root")
                || SecurityUtils.getSubject().hasRole("problem_admin")
                || SecurityUtils.getSubject().hasRole("admin");

        if (!StringUtils.isEmpty(pid)) {
            discussionQueryWrapper.eq("pid", pid);
            QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("problem_id", pid);
            Problem problem = problemEntityService.getOne(problemQueryWrapper);

            if (!problem.getIsPublic() && !isAdmin && !groupValidator.isGroupMember(userRolesVo.getUid(), problem.getGid())) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        } else if (!(admin && isAdmin)) {
            discussionQueryWrapper.isNull("gid");
        }

        discussionQueryWrapper
                .eq(!(admin && isAdmin), "status", 0)
                .orderByDesc("top_priority")
                .orderByDesc("gmt_create")
                .orderByDesc("like_num")
                .orderByDesc("view_num");

        if (onlyMine) {
            discussionQueryWrapper.eq("uid", userRolesVo.getUid());
        }

        return discussionEntityService.page(iPage, discussionQueryWrapper);
    }

    public DiscussionVo getDiscussion(Integer did) throws StatusNotFoundException, StatusForbiddenException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Discussion discussion = discussionEntityService.getById(did);

        if (discussion.getGid() != null) {
            if (!isRoot && !discussion.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), discussion.getGid())) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        String uid = null;

        if (userRolesVo != null) {
            uid = userRolesVo.getUid();
        }

        DiscussionVo discussionVo = discussionEntityService.getDiscussion(did, uid);

        if (discussionVo == null) {
            throw new StatusNotFoundException("对不起，该讨论不存在！");
        }

        if (discussionVo.getStatus() == 1) {
            throw new StatusForbiddenException("对不起，该讨论已被封禁！");
        }

        // 浏览量+1
        UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
        discussionUpdateWrapper.setSql("view_num=view_num+1").eq("id", discussionVo.getId());
        discussionEntityService.update(discussionUpdateWrapper);
        discussionVo.setViewNum(discussionVo.getViewNum() + 1);

        return discussionVo;
    }

    public void addDiscussion(Discussion discussion) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {

        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        String problemId = discussion.getPid();
        if (problemId != null) {
            QueryWrapper<Problem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("problemId", problemId);
            Problem problem = problemEntityService.getOne(problemQueryWrapper);

            if (problem == null) {
                throw new StatusNotFoundException("该题目不存在");
            } else if (!problem.getIsPublic()) {
                discussion.setGid(problem.getGid());
            }
        }

        if (discussion.getGid() != null) {
            if (!isRoot && !discussion.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), discussion.getGid())) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        // 除管理员外 其它用户需要AC20道题目以上才可发帖，同时限制一天只能发帖5次
        if (!isRoot && !isProblemAdmin && !isAdmin) {
            QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
            int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);

            if (userAcProblemCount < 10 && userRolesVo.getMobile() == null) {
                throw new StatusForbiddenException("对不起，您暂时不能评论！请先去绑定手机号或提交题目通过10道以上!");
            }

            String lockKey = Constants.Account.DISCUSSION_ADD_NUM_LOCK.getCode() + userRolesVo.getUid();
            Integer num = (Integer) redisUtils.get(lockKey);
            if (num == null) {
                redisUtils.set(lockKey, 1, 3600 * 24);
            } else if (num >= 5) {
                throw new StatusForbiddenException("对不起，您今天发帖次数已超过5次，已被限制！");
            } else {
                redisUtils.incr(lockKey, 1);
            }
        }

        discussion.setAuthor(userRolesVo.getUsername())
                .setAvatar(userRolesVo.getAvatar())
                .setUid(userRolesVo.getUid());

        if (SecurityUtils.getSubject().hasRole("root")) {
            discussion.setRole("root");
        } else if (SecurityUtils.getSubject().hasRole("admin")
                || SecurityUtils.getSubject().hasRole("problem_admin")) {
            discussion.setRole("admin");
        } else {
            // 如果不是管理员角色，一律重置为不置顶
            discussion.setTopPriority(false);
        }

        boolean isOk = discussionEntityService.saveOrUpdate(discussion);
        if (!isOk) {
            throw new StatusFailException("发布失败，请重新尝试！");
        }
    }

    public void updateDiscussion(Discussion discussion) throws StatusFailException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        if (!isRoot && !discussion.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupAdmin(userRolesVo.getUid(), discussion.getGid())) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        boolean isOk = discussionEntityService.updateById(discussion);
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    public void removeDiscussion(Integer did) throws StatusFailException, StatusForbiddenException {
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Discussion discussion = discussionEntityService.getById(did);

        if (!isRoot && !discussion.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupAdmin(userRolesVo.getUid(), discussion.getGid())) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<Discussion>().eq("id", did);
        // 如果不是是管理员,则需要附加当前用户的uid条件
        if (!SecurityUtils.getSubject().hasRole("root")
                && !SecurityUtils.getSubject().hasRole("admin")
                && !SecurityUtils.getSubject().hasRole("problem_admin")) {
            discussionUpdateWrapper.eq("uid", userRolesVo.getUid());
        }
        boolean isOk = discussionEntityService.remove(discussionUpdateWrapper);
        if (!isOk) {
            throw new StatusFailException("删除失败，无权限或者该讨论不存在");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void addDiscussionLike(Integer did, Boolean toLike) throws StatusFailException, StatusForbiddenException {
        // 获取当前登录的用户
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Discussion discussion = discussionEntityService.getById(did);
        if (!isRoot && !discussion.getUid().equals(userRolesVo.getUid()) && !groupValidator.isGroupMember(userRolesVo.getUid(), discussion.getGid())) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<DiscussionLike> discussionLikeQueryWrapper = new QueryWrapper<>();
        discussionLikeQueryWrapper.eq("did", did).eq("uid", userRolesVo.getUid());

        DiscussionLike discussionLike = discussionLikeEntityService.getOne(discussionLikeQueryWrapper, false);

        if (toLike) { // 添加点赞
            if (discussionLike == null) { // 如果不存在就添加
                boolean isSave = discussionLikeEntityService.saveOrUpdate(new DiscussionLike().setUid(userRolesVo.getUid()).setDid(did));
                if (!isSave) {
                    throw new StatusFailException("点赞失败，请重试尝试！");
                }
            }
            // 点赞+1
            Discussion discussion1 = discussionEntityService.getById(did);
            UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
            discussionUpdateWrapper.eq("id", discussion.getId())
                    .setSql("like_num=like_num+1");
            discussionEntityService.update(discussionUpdateWrapper);
            // 当前帖子要不是点赞者的 才发送点赞消息
            if (!userRolesVo.getUsername().equals(discussion.getAuthor())) {
                discussionEntityService.updatePostLikeMsg(discussion.getUid(), userRolesVo.getUid(), did);
            }
        } else { // 取消点赞
            if (discussionLike != null) { // 如果存在就删除
                boolean isDelete = discussionLikeEntityService.removeById(discussionLike.getId());
                if (!isDelete) {
                    throw new StatusFailException("取消点赞失败，请重试尝试！");
                }
            }
            // 点赞-1
            UpdateWrapper<Discussion> discussionUpdateWrapper = new UpdateWrapper<>();
            discussionUpdateWrapper.setSql("like_num=like_num-1").eq("id", did);
            discussionEntityService.update(discussionUpdateWrapper);
        }

    }

    public List<Category> getDiscussionCategory() {
        return categoryEntityService.list();
    }

    public void addDiscussionReport(DiscussionReport discussionReport) throws StatusFailException {
        boolean isOk = discussionReportEntityService.saveOrUpdate(discussionReport);
        if (!isOk) {
            throw new StatusFailException("举报失败，请重新尝试");
        }
    }
}