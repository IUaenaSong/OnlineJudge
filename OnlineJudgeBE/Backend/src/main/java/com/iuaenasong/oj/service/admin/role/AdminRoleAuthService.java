/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.role;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.pojo.entity.user.RoleAuth;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AdminRoleAuthService {

    public CommonResult<IPage<Auth>> getAuthList(Integer limit, Integer currentPage, Long rid, String keyword);

    public CommonResult<Void> addAuth(RoleAuth roleAuth);

    public CommonResult<Void> deleteAuth(Long aid, Long rid);

}
