/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.auth;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AdminAuthService {

    public CommonResult<IPage<Auth>> getAuthList(Integer limit, Integer currentPage, String keyword);

    public CommonResult<Void> deleteAuth(Long aid);

    public CommonResult<Void> addAuth(Auth auth);

    public CommonResult<Void> updateAuth(Auth auth);

    public CommonResult<Void> changeAuthStatus(Long aid, Integer status);
}
