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
import com.iuaenasong.oj.pojo.vo.ConfigVo;
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

    @Autowired
    private ConfigVo configVo;

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
            throw new StatusNotFoundException("????????????????????????????????????");
        }
        if (!group.getVisible() && !isRoot && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("????????????????????????????????????????????????");
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
                throw new StatusNotFoundException("????????????????????????????????????");
            }
            if (!isRoot && !group.getVisible() && !groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
                throw new StatusForbiddenException("????????????????????????????????????????????????");
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

            if (userAcProblemCount < configVo.getDefaultCreateGroupACInitValue() && userRolesVo.getMobile() == null) {
                throw new StatusForbiddenException("??????????????????????????????????????????????????????????????????????????????????????????" +
                        configVo.getDefaultCreateGroupACInitValue() + "?????????!");
            }

            String lockKey = Constants.Account.GROUP_ADD_NUM_LOCK.getCode() + userRolesVo.getUid();
            Integer num = (Integer) redisUtils.get(lockKey);
            if (num == null) {
                redisUtils.set(lockKey, 1, 3600 * 24);
            } else if (num >= configVo.getDefaultCreateGroupDailyLimit()) {
                throw new StatusForbiddenException("????????????????????????????????????????????????" + configVo.getDefaultCreateGroupDailyLimit() + "?????????????????????");
            } else {
                redisUtils.incr(lockKey, 1);
            }

            QueryWrapper<Group> existedGroupQueryWrapper = new QueryWrapper<>();
            existedGroupQueryWrapper.eq("owner", userRolesVo.getUsername());
            int existedGroupNum = groupEntityService.count(existedGroupQueryWrapper);

            if (existedGroupNum >= configVo.getDefaultCreateGroupLimit()) {
                throw new StatusForbiddenException("?????????????????????????????????" + configVo.getDefaultCreateGroupLimit() + "?????????????????????????????????????????????");
            }
        }
        group.setOwner(userRolesVo.getUsername());
        group.setUid(userRolesVo.getUid());

        if (StringUtils.isEmpty(group.getName()) || group.getName().length() < 5 || group.getName().length() > 25) {
            throw new StatusFailException("??????????????????????????????????????? 5 ??? 25???");
        }

        if (StringUtils.isEmpty(group.getShortName()) || group.getShortName().length() < 5 || group.getShortName().length() > 10) {
            throw new StatusFailException("??????????????????????????????????????? 5 ??? 10???");
        }

        if (StringUtils.isEmpty(group.getBrief()) || group.getBrief().length() < 5 || group.getBrief().length() > 50) {
            throw new StatusFailException("??????????????????????????????????????? 5 ??? 50???");
        }

        if (group.getAuth() == null || group.getAuth() < 1 || group.getAuth() > 3) {
            throw new StatusFailException("?????????????????????????????????1~3???");
        }

        if (group.getAuth() == 3) {
            if (StringUtils.isEmpty(group.getCode()) || group.getCode().length() != 6) {
                throw new StatusFailException("?????????????????????????????????????????? 6???");
            }
        }

        if (StringUtils.isEmpty(group.getDescription()) || group.getDescription().length() < 5 || group.getDescription().length() > 1000) {
            throw new StatusFailException("??????????????????????????????????????? 5 ??? 1000???");
        }

        QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("name", group.getName());

        Group group1 = groupEntityService.getOne(groupQueryWrapper);

        if (group1 != null) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("short_name", group.getShortName());

        group1 = groupEntityService.getOne(groupQueryWrapper);

        if (group1 != null) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        boolean isOk = groupEntityService.save(group);
        if (!isOk) {
            throw new StatusFailException("?????????????????????????????????");
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
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        if (StringUtils.isEmpty(group.getName()) || group.getName().length() < 5 || group.getName().length() > 25) {
            throw new StatusFailException("??????????????????????????????????????? 5 ??? 25???");
        }

        if (StringUtils.isEmpty(group.getShortName()) || group.getShortName().length() < 5 || group.getShortName().length() > 10) {
            throw new StatusFailException("??????????????????????????????????????? 5 ??? 10???");
        }

        if (StringUtils.isEmpty(group.getBrief()) || group.getBrief().length() < 5 || group.getBrief().length() > 50) {
            throw new StatusFailException("??????????????????????????????????????? 5 ??? 50???");
        }

        if (group.getAuth() == null || group.getAuth() < 1 || group.getAuth() > 3) {
            throw new StatusFailException("?????????????????????????????????1~3???");
        }

        if (group.getAuth() == 3) {
            if (StringUtils.isEmpty(group.getCode()) || group.getCode().length() != 6) {
                throw new StatusFailException("?????????????????????????????????????????? 6???");
            }
        }

        if (StringUtils.isEmpty(group.getDescription()) || group.getDescription().length() < 5 || group.getDescription().length() > 1000) {
            throw new StatusFailException("??????????????????????????????????????? 5 ??? 1000???");
        }

        QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("name", group.getName()).ne("id", group.getId());;

        Group group1 = groupEntityService.getOne(groupQueryWrapper);

        if (group1 != null && group1.getId().longValue() != group.getId()) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.eq("short_name", group.getShortName()).ne("id", group.getId());;

        group1 = groupEntityService.getOne(groupQueryWrapper);

        if (group1 != null && group1.getId().longValue() != group.getId()) {
            throw new StatusFailException("?????????????????????????????????????????????");
        }

        Boolean isOk = groupEntityService.updateById(group);
        if (!isOk) {
            throw new StatusFailException("?????????????????????????????????");
        }
    }

    public void deleteGroup(Long gid) throws StatusFailException, StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("????????????????????????????????????");
        }

        if (!group.getUid().equals(userRolesVo.getUid()) && !isRoot) {
            throw new StatusForbiddenException("?????????????????????????????????");
        }

        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).in("auth", 3, 4, 5);
        List<GroupMember> groupMemberList = groupMemberEntityService.list(groupMemberQueryWrapper);
        List<String> groupMemberUidList = groupMemberList.stream().map(GroupMember::getUid).collect(Collectors.toList());

        Boolean isOk = groupEntityService.removeById(gid);
        if (!isOk) {
            throw new StatusFailException("?????????????????????????????????");
        } else {
            groupMemberEntityService.addDissolutionNoticeToGroupMember(gid,
                    group.getName(),
                    groupMemberUidList,
                    userRolesVo.getUsername());
        }
    }
}
