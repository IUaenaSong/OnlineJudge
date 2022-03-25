/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.role;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.user.Role;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AdminRoleService {

    public CommonResult<IPage<Role>> getRoleList(Integer limit, Integer currentPage, String keyword);
}
