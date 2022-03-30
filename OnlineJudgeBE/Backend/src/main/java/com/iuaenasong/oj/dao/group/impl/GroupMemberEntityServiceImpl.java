/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.manager.msg.AdminNoticeManager;
import com.iuaenasong.oj.mapper.GroupMemberMapper;
import com.iuaenasong.oj.pojo.entity.group.GroupMember;
import com.iuaenasong.oj.pojo.entity.user.UserInfo;
import com.iuaenasong.oj.pojo.vo.GroupMemberVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupMemberEntityServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberEntityService {

    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private AdminNoticeManager adminNoticeManager;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    @Override
    public IPage<GroupMemberVo> getMemberList(int limit, int currentPage, String keyword, Integer auth, Long gid) {
        IPage<GroupMemberVo> iPage = new Page<>(currentPage, limit);
        List<GroupMemberVo> memberList = groupMemberMapper.getMemberList(iPage, keyword, auth, gid);

        return iPage.setRecords(memberList);
    }

    @Override
    public IPage<GroupMemberVo> getApplyList(int limit, int currentPage, String keyword, Integer auth, Long gid) {
        IPage<GroupMemberVo> iPage = new Page<>(currentPage, limit);
        List<GroupMemberVo> applyList = groupMemberMapper.getApplyList(iPage, keyword, auth, gid);

        return iPage.setRecords(applyList);
    }

    @Override
    public List<String> getGroupRootUidList(Long gid) {
        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("auth", 5);
        List<GroupMember> groupMembers = groupMemberMapper.selectList(groupMemberQueryWrapper);
        return groupMembers.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    @Async
    @Override
    public void addApplyNoticeToGroupRoot(Long gid, String groupName, String newMemberUid) {
        String title = "团队成员申请通知(Group Member Application Notice)";
        UserInfo newMemberUser = userInfoEntityService.getById(newMemberUid);
        if (newMemberUser != null) {
            String content = getNewMemberApplyGroupContent(gid, groupName, newMemberUser);
            List<String> groupRootUidList = getGroupRootUidList(gid);
            adminNoticeManager.addSingleNoticeToBatchUser(null, groupRootUidList, title, content, "Mine");
        }
    }

    private String getNewMemberApplyGroupContent(Long gid, String groupName, UserInfo newMemberUser) {
        return "您好，您作为[" + gid + "]【" + groupName
                + "】团队的超级管理员，目前有用户【" + newMemberUser.getUsername()
                + "】正在申请参加该团队，请前往查看审批！" +
                "\n\n" +
                "Hello, as the super administrator of the [" + gid + "]【" + groupName
                + "】 group, a user 【" + newMemberUser.getUsername()
                + "】 is applying to join the group. Please go to check and approve!";
    }

    @Async
    @Override
    public void addWelcomeNoticeToGroupNewMember(Long gid, String groupName,String memberUid) {
        String title = "欢迎加入团队(Welcome to The Group)";
        String content = getWelcomeNewMember(gid, groupName);
        adminNoticeManager.addSingleNoticeToUser(null, memberUid, title, content, "Mine");
    }

    public String getWelcomeNewMember(Long gid, String groupName) {
        return "您好，您已经通过了审批，欢迎加入[" + gid + "]【" + groupName
                + "】团队！" +
                "\n\n" +
                "Hello, you have passed the approval. Welcome to join the group [" + gid +
                "]【" + groupName + "】!";
    }

    @Async
    @Override
    public void addRemoveNoticeToGroupMember(Long gid, String groupName, String operator, String memberUid) {
        String title = "移除团队成员通知(Remove Group Member Notice)";
        String content = getRemoveMemberContent(gid, groupName, operator);
        adminNoticeManager.addSingleNoticeToUser(null, memberUid, title, content, "Mine");
    }

    public String getRemoveMemberContent(Long gid, String groupName, String operator) {
        return "您好，您已经被[" + gid + "]【" + groupName
                + "】团队的管理员【" + operator + "】移除了团队！" +
                "\n\n" +
                "Hello, You have been removed from the group [" + gid +
                "]【" + groupName + "】 by the group admin 【" + operator + "】!";
    }

    @Async
    @Override
    public void addDissolutionNoticeToGroupMember(Long gid, String groupName, List<String> groupMemberUidList, String operator) {
        String title = "团队解散通知(Group Dissolution Notice)";
        String content = getDissolutionGroupContent(gid, groupName, operator);
        adminNoticeManager.addSingleNoticeToBatchUser(null, groupMemberUidList, title, content, "Mine");

    }

    private String getDissolutionGroupContent(Long gid, String groupName, String operator) {
        return "您好，您所在的团队[" + gid + "]【" + groupName
                + "】目前已经被管理员【" + operator + "】解散，请您注意！" +
                "\n\n" +
                "Hello, your team [" + gid + "]【" + groupName
                + "】 has been dissolved by the administrator 【" + operator
                + "】, please pay attention!";
    }
}
