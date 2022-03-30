/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.validator;

import com.iuaenasong.oj.dao.group.GroupEntityService;
import com.iuaenasong.oj.dao.group.GroupMemberEntityService;
import com.iuaenasong.oj.dao.user.UserInfoEntityService;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.entity.group.GroupMember;

import com.iuaenasong.oj.pojo.entity.user.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupValidator {

    @Autowired
    private GroupMemberEntityService groupMemberEntityService;

    @Autowired
    private GroupEntityService groupEntityService;

    @Autowired
    private UserInfoEntityService userInfoEntityService;

    public boolean isGroupMember(String uid, Long gid) {
        if (uid == null || gid == null) {
            return false;
        }
        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("uid", uid).in("auth", 3, 4, 5);

        GroupMember groupMember = groupMemberEntityService.getOne(groupMemberQueryWrapper, false);

        return groupMember != null || isGroupOwner(uid, gid);
    }

    public boolean isGroupAdmin(String uid, Long gid) {
        if (uid == null || gid == null) {
            return false;
        }
        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("uid", uid).in("auth", 4, 5);

        GroupMember groupMember = groupMemberEntityService.getOne(groupMemberQueryWrapper, false);

        return groupMember != null || isGroupOwner(uid, gid);
    }

    public boolean isGroupRoot(String uid, Long gid) {
        if (uid == null || gid == null) {
            return false;
        }
        QueryWrapper<GroupMember> groupMemberQueryWrapper = new QueryWrapper<>();
        groupMemberQueryWrapper.eq("gid", gid).eq("uid", uid).in("auth", 5);

        GroupMember groupMember = groupMemberEntityService.getOne(groupMemberQueryWrapper,false);

        return groupMember != null || isGroupOwner(uid, gid);
    }

    public boolean isGroupOwner(String uid, Long gid) {
        if (uid == null || gid == null) {
            return false;
        }

        Group group = groupEntityService.getById(gid);

        return group != null && group.getUid().equals(uid);
    }
}