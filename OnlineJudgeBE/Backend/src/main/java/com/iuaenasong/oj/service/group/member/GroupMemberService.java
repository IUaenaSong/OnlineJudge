/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group.member;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.group.GroupMember;
import com.iuaenasong.oj.pojo.vo.GroupMemberVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface GroupMemberService {

    public CommonResult<IPage<GroupMemberVo>> getMemberList(Integer limit, Integer currentPage, String keyword, Integer auth, Long gid);

    public CommonResult<IPage<GroupMemberVo>> getApplyList(Integer limit, Integer currentPage, String keyword, Integer auth, Long gid);

    public CommonResult<Void> addMember(String uid, Long gid, String code, String reason);

    public CommonResult<Void> updateMember(GroupMember groupMember);

    public CommonResult<Void> deleteMember(String uid, Long gid);

    public CommonResult<Void> exitGroup(Long gid);
}
