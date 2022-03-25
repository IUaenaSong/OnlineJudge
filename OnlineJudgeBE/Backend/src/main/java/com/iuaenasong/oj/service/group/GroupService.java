/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.group;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.group.Group;
import com.iuaenasong.oj.pojo.vo.AccessVo;
import com.iuaenasong.oj.pojo.vo.GroupVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface GroupService {

    public CommonResult<IPage<GroupVo>> getGroupList(Integer limit, Integer currentPage, String keyword, Integer auth, Boolean onlyMine);

    public CommonResult<Group> getGroup(Long gid);

    public CommonResult<AccessVo> getGroupAccess(Long gid);

    public CommonResult<Integer> getGroupAuth(Long gid);

    public CommonResult<Void> addGroup(Group group);

    public CommonResult<Void> updateGroup(Group group);

    public CommonResult<Void> deleteGroup(Long gid);
}
