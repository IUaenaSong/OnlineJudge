/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.group;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.dao.user.UserAcproblemEntityService;
import com.iuaenasong.oj.pojo.entity.group.GroupMember;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.entity.user.UserAcproblem;
import com.iuaenasong.oj.pojo.vo.AccessVo;
import com.iuaenasong.oj.pojo.vo.GroupVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.utils.Constants;
import com.iuaenasong.oj.utils.RedisUtils;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private UserAcproblemEntityService userAcproblemEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private GroupValidator groupValidator;

    public IPage<GroupVo> getGroupList(Integer limit, Integer currentPage, String keyword, Integer auth, Boolean onlyMine) {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (auth == null || auth < 1) auth = 0;

        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
        }

        String uid = "";
        if (userRolesVo != null) {
            uid = userRolesVo.getUid();
        }

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        return groupEntityService.getGroupList(limit, currentPage, keyword, auth, uid, onlyMine, isRoot);
    }

    public Group getGroup(Long gid) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);
        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }
        if (!group.getVisible() && !isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("该团队并非公开团队，不支持访问！");
        }

        if (!isRoot && !groupValidator.isGroupRoot(userRolesVo.getUid(), gid))  {
            group.setCode(null);
        }

        return group;
    }

    public AccessVo getGroupAccess(Long gid) throws StatusFailException, StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        boolean access = false;

        if (groupValidator.isGroupMember(userRolesVo.getUid(), gid) || isRoot) {
            access = true;
            Group group = groupEntityService.getById(gid);
            if (group == null || group.getStatus() == 1 && !isRoot) {
                throw new StatusNotFoundException("该团队不存在或已被封禁！");
            }
            if (!isRoot && !group.getVisible() && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("该团队并非公开团队，不支持访问！");
            }
        }

        AccessVo accessVo = new AccessVo();
        accessVo.setAccess(access);
        return accessVo;
    }

    public Integer getGroupAuth(Long gid) throws StatusFailException, StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("uid", userRolesVo.getUid());

        GroupMember groupMember = groupMemberEntityService.getOne(groupMemberQueryWrapper);

        Integer auth = 0;
        if (groupMember != null) {
            auth = groupMember.getAuth();
        }

        if (groupValidator.isGroupOwner(userRolesVo.getUid(), gid)) {
            auth = 5;
        }
        return auth;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGroup(Group group) throws StatusFailException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");
        boolean isProblemAdmin = SecurityUtils.getSubject().hasRole("problem_admin");
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin");

        if (!isRoot && !isAdmin && !isProblemAdmin) {

            QueryWrapper<UserAcproblem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", userRolesVo.getUid()).select("distinct pid");
            int userAcProblemCount = userAcproblemEntityService.count(queryWrapper);

            if (userAcProblemCount < 20 && userRolesVo.getMobile() == null) {
                throw new StatusForbiddenException("对不起，您暂时无权限创建团队！请先去绑定手机号或提交题目通过20道以上!");
            }

            String lockKey = Constants.Account.GROUP_ADD_NUM_LOCK.getCode() + userRolesVo.getUid();
            Integer num = (Integer) redisUtils.get(lockKey);
            if (num == null) {
                redisUtils.set(lockKey, 1, 3600 * 24);
            } else if (num >= 2) {
                throw new StatusForbiddenException("对不起，您今天创建团队次数已超过2次，已被限制！");
            } else {
                redisUtils.incr(lockKey, 1);
            }
        }
        group.setOwner(userRolesVo.getUsername());
        group.setUid(userRolesVo.getUid());

        if (StringUtils.isEmpty(group.getName()) || group.getName().length() < 5 || group.getName().length() > 25) {
            throw new StatusFailException("团队名称不能为空且长度应为 5 到 25！");
        }

        if (StringUtils.isEmpty(group.getShortName()) || group.getShortName().length() < 5 || group.getShortName().length() > 10) {
            throw new StatusFailException("团队简称不能为空且长度应为 5 到 10！");
        }

        if (StringUtils.isEmpty(group.getBrief()) || group.getBrief().length() < 5 || group.getBrief().length() > 50) {
            throw new StatusFailException("团队简介不能为空且长度应为 5 到 50！");
        }

        if (group.getAuth() == null || group.getAuth() < 1 || group.getAuth() > 3) {
            throw new StatusFailException("团队权限不能为空且应为1~3！");
        }

        if (group.getAuth() == 2 || group.getAuth() == 3) {
            if (StringUtils.isEmpty(group.getCode()) || group.getCode().length() != 6) {
                throw new StatusFailException("团队邀请码不能为空且长度应为 6！");
            }
        }

        if (StringUtils.isEmpty(group.getDescription()) || group.getDescription().length() < 5 || group.getDescription().length() > 1000) {
            throw new StatusFailException("团队描述不能为空且长度应为 5 到 1000！");
        }

        QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("name", group.getName());

        Group group1 = groupEntityService.getOne(groupQueryWrapper);

        if (group1 != null) {
            throw new StatusFailException("团队名称已存在，请修改后重试！");
        }

        groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("short_name", group.getShortName());

        group1 = groupEntityService.getOne(groupQueryWrapper);

        if (group1 != null) {
            throw new StatusFailException("团队简称已存在，请修改后重试！");
        }

        boolean isOk = groupEntityService.save(group);
        if (!isOk) {
            throw new StatusFailException("创建失败，请重新尝试！");
        } else {
            groupMemberEntityService.save(new GroupMember()
                    .setUid(userRolesVo.getUid())
                    .setGid(group.getId())
                    .setAuth(5));
        }
    }

    public void updateGroup(Group group) throws StatusFailException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        if (!groupValidator.isGroupRoot(userRolesVo.getUid(), group.getId()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        if (StringUtils.isEmpty(group.getName()) || group.getName().length() < 5 || group.getName().length() > 25) {
            throw new StatusFailException("团队名称不能为空且长度应为 5 到 25！");
        }

        if (StringUtils.isEmpty(group.getShortName()) || group.getShortName().length() < 5 || group.getShortName().length() > 10) {
            throw new StatusFailException("团队简称不能为空且长度应为 5 到 10！");
        }

        if (StringUtils.isEmpty(group.getBrief()) || group.getBrief().length() < 5 || group.getBrief().length() > 50) {
            throw new StatusFailException("团队简介不能为空且长度应为 5 到 50！");
        }

        if (group.getAuth() == null || group.getAuth() < 1 || group.getAuth() > 3) {
            throw new StatusFailException("团队权限不能为空且应为1~3！");
        }

        if (group.getAuth() == 2 || group.getAuth() == 3) {
            if (StringUtils.isEmpty(group.getCode()) || group.getCode().length() != 6) {
                throw new StatusFailException("团队邀请码不能为空且长度应为 6！");
            }
        }

        if (StringUtils.isEmpty(group.getDescription()) || group.getDescription().length() < 5 || group.getDescription().length() > 1000) {
            throw new StatusFailException("团队描述不能为空且长度应为 5 到 1000！");
        }

        QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("name", group.getName()).ne("id", group.getId());;

        Group group1 = groupEntityService.getOne(groupQueryWrapper);

        if (group1 != null && group1.getId().longValue() != group.getId()) {
            throw new StatusFailException("团队名称已存在，请修改后重试！");
        }

        groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("short_name", group.getShortName()).ne("id", group.getId());;

        group1 = groupEntityService.getOne(groupQueryWrapper);

        if (group1 != null && group1.getId().longValue() != group.getId()) {
            throw new StatusFailException("团队简称已存在，请修改后重试！");
        }

        Boolean isOk = groupEntityService.updateById(group);
        if (!isOk) {
            throw new StatusFailException("更新失败，请重新尝试！");
        }
    }

    public void deleteGroup(Long gid) throws StatusFailException, StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!group.getUid().equals(userRolesVo.getUid()) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).in("auth", 3, 4, 5);
        List<GroupMember> groupMemberList = groupMemberEntityService.list(groupMemberQueryWrapper);
        List<String> groupMemberUidList = groupMemberList.stream().map(GroupMember::getUid).collect(Collectors.toList());

        Boolean isOk = groupEntityService.removeById(gid);
        if (!isOk) {
            throw new StatusFailException("删除失败，请重新尝试！");
        } else {
            groupMemberEntityService.addDissolutionNoticeToGroupMember(gid,
                    group.getName(),
                    groupMemberUidList,
                    userRolesVo.getUsername());
        }
    }
}
