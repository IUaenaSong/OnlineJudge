/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.dao.group;

import com.iuaenasong.oj.pojo.entity.group.GroupMember;
import com.iuaenasong.oj.pojo.vo.GroupMemberVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GroupMemberEntityService extends IService<GroupMember> {
    IPage<GroupMemberVo> getMemberList(int limit, int currentPage, String keyword, Integer auth, Long gid);
    IPage<GroupMemberVo> getApplyList(int limit, int currentPage, String keyword, Integer auth, Long gid);
}
