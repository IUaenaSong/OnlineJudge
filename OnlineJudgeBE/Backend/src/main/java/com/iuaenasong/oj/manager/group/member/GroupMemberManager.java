/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.manager.group.member;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.manager.group.GroupManager;
import com.iuaenasong.oj.pojo.entity.group.GroupMember;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.vo.GroupMemberVo;
import com.iuaenasong.oj.pojo.vo.UserRolesVo;
import com.iuaenasong.oj.validator.GroupValidator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupMemberManager {

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Autowired
    private GroupManager groupManager;

    @Autowired
    private GroupValidator groupValidator;

    public IPage<GroupMemberVo> getMemberList(Integer limit, Integer currentPage, String keyword, Integer auth, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupMember(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (auth == null || auth < 1) auth = 0;

        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
        }

        return groupMemberEntityService.getMemberList(limit, currentPage, keyword, auth, gid);
    }

    public IPage<GroupMemberVo> getApplyList(Integer limit, Integer currentPage, String keyword, Integer auth, Long gid) throws StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupAdmin(userRolesVo.getUid(), gid) && !isRoot) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        if (currentPage == null || currentPage < 1) currentPage = 1;
        if (limit == null || limit < 1) limit = 10;
        if (auth == null || auth < 1) auth = 0;

        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.trim();
        }

        return groupMemberEntityService.getApplyList(limit, currentPage, keyword, auth, gid);
    }

    public void addMember(String uid, Long gid, String code, String reason) throws StatusFailException, StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || (group.getStatus() == 1 || !group.getVisible()) && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁或未公开显示！");
        }

        if (!userRolesVo.getUid().equals(uid) && !group.getUid().equals(userRolesVo.getUid())) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("uid", uid).eq("gid", gid);

        GroupMember groupMember = groupMemberEntityService.getOne(groupMemberQueryWrapper);

        if (groupMember != null && groupMember.getAuth() == 1) {
            throw new StatusForbiddenException("您已申请过，请勿重复申请！");
        }

        if (groupValidator.isGroupMember(uid, gid)) {
            throw new StatusForbiddenException("您已经加入了该团队！");
        }

        if (group.getAuth() == 3 && !code.equals(group.getCode())) {
            throw new StatusFailException("邀请码错误，请重新尝试！");
        }

        if (group.getAuth() != 1 && !StringUtils.isEmpty(reason) && (reason.length() < 5 || reason.length() > 100)) {
            throw new StatusFailException("申请理由的长度应为 5 到 100！");
        }

        boolean isOk = false;
        if (groupMember != null) {
            UpdateWrapper<GroupMember> groupMemberUpdateWrapper = new UpdateWrapper<>();
            groupMemberUpdateWrapper.eq("uid", uid).eq("gid", gid).set("reason", reason);
            if (group.getAuth() == 1) {
                groupMemberUpdateWrapper.set("auth", 3);
            } else {
                groupMemberUpdateWrapper.set("auth", 1);
            }
            isOk = groupMemberEntityService.update(groupMemberUpdateWrapper);
        } else {
            GroupMember groupMember1 = new GroupMember();
            groupMember1.setUid(uid).setGid(gid).setReason(reason);
            if (group.getAuth() == 1) {
                groupMember1.setAuth(3);
            } else {
                groupMember1.setAuth(1);
            }
            isOk = groupMemberEntityService.save(groupMember1);
        }
        if (!isOk) {
            throw new StatusFailException("申请失败，请重新尝试！");
        }  else {
            if (group.getAuth() == 1) {
                groupMemberEntityService.addWelcomeNoticeToGroupNewMember(gid, group.getName(), userRolesVo.getUid());
            } else {
                groupMemberEntityService.addApplyNoticeToGroupRoot(gid, group.getName(), userRolesVo.getUid());
            }
        }
    }

    public void updateMember(GroupMember groupMemberDto) throws StatusFailException, StatusForbiddenException, StatusNotFoundException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Long gid = groupMemberDto.getGid();

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("uid", userRolesVo.getUid()).in("auth", 4, 5);

        GroupMember groupMember = groupMemberEntityService.getOne(groupMemberQueryWrapper);

        if (groupMember == null && !isRoot && !group.getUid().equals(userRolesVo.getUid())) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<GroupMember> groupMemberQueryWrapper1 = new QueryWrapper<>();
        groupMemberQueryWrapper1.eq("gid", gid).eq("uid", groupMemberDto.getUid());

        GroupMember groupMember1 = groupMemberEntityService.getOne(groupMemberQueryWrapper1);

        if (groupMember1 == null) {
            throw new StatusNotFoundException("该用户不在团队中！");
        }

        if (!isRoot && !group.getUid().equals(userRolesVo.getUid())) {
            if (groupMember1.getAuth() >= groupMember.getAuth() || groupMemberDto.getAuth() >= groupMember.getAuth() || !group.getUid().equals(groupMemberDto.getUid())) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        Boolean isOk = groupMemberEntityService.updateById(groupMemberDto);
        if (!isOk) {
            throw new StatusFailException("更新失败，请重新尝试！");
        } else {
            if (groupMember1.getAuth() <= 2 && groupMemberDto.getAuth() >= 3) { // 之前是申请中，则之后通过审批就要发消息
                groupMemberEntityService.addWelcomeNoticeToGroupNewMember(gid, group.getName(), groupMemberDto.getUid());
            }
        }
    }

    public void deleteMember(String uid, Long gid) throws StatusFailException, StatusNotFoundException, StatusForbiddenException {
        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (group.getUid().equals(uid)) {
            throw new StatusForbiddenException("对不起，您无权限删除团队负责人！");
        }
        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("uid", userRolesVo.getUid()).in("auth", 3, 4, 5);

        GroupMember groupMember = groupMemberEntityService.getOne(groupMemberQueryWrapper);

        if (groupMember == null && !isRoot && !group.getUid().equals(userRolesVo.getUid())) {
            throw new StatusForbiddenException("对不起，您无权限操作！");
        }

        QueryWrapper<GroupMember> groupMemberQueryWrapper1 = new QueryWrapper<>();
        groupMemberQueryWrapper1.eq("gid", gid).eq("uid", uid).in("auth", 3, 4, 5);

        GroupMember groupMember1 = groupMemberEntityService.getOne(groupMemberQueryWrapper1);

        if (groupMember1 == null) {
            throw new StatusNotFoundException("该用户不在团队中！");
        }

        if (!isRoot && !group.getUid().equals(userRolesVo.getUid())) {
            if (groupMember1.getAuth() >= groupMember.getAuth()) {
                throw new StatusForbiddenException("对不起，您无权限操作！");
            }
        }

        Boolean isOk = groupMemberEntityService.remove(groupMemberQueryWrapper1);
        if (!isOk) {
            throw new StatusFailException("删除失败，请重新尝试！");
        } else {
            groupMemberEntityService.addRemoveNoticeToGroupMember(gid, group.getName(), userRolesVo.getUsername(), uid);
        }
    }

    public void exitGroup(Long gid) throws StatusNotFoundException, StatusForbiddenException, StatusFailException {

        Session session = SecurityUtils.getSubject().getSession();
        UserRolesVo userRolesVo = (UserRolesVo) session.getAttribute("userInfo");

        boolean isRoot = SecurityUtils.getSubject().hasRole("root");

        Group group = groupEntityService.getById(gid);

        if (group == null || group.getStatus() == 1 && !isRoot) {
            throw new StatusNotFoundException("该团队不存在或已被封禁！");
        }

        if (!groupValidator.isGroupMember(userRolesVo.getUid(), gid)) {
            throw new StatusForbiddenException("对不起，您并未在当前团队，无法退出！");
        }

        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid)
                .eq("uid", userRolesVo.getUid());

        boolean isOk = groupMemberEntityService.remove(groupMemberQueryWrapper);
        if (!isOk) {
            throw new StatusFailException("退出团队失败，请重新尝试！");
        } else {
            if (group.getUid().equals(userRolesVo.getUid())) {
                List<String> groupOwnerUidList = new ArrayList<>();
                groupOwnerUidList.add(userRolesVo.getUid());
                groupMemberEntityService.addDissolutionNoticeToGroupMember(gid,
                        group.getName(),
                        groupOwnerUidList,
                        userRolesVo.getUsername());
                groupManager.deleteGroup(gid);
            }
        }
    }
}
