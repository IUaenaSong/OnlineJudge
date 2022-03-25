/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.account;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.LoginDto;
import com.iuaenasong.oj.pojo.vo.UserInfoVo;

public interface AdminAccountService {

    public CommonResult<UserInfoVo> login(LoginDto loginDto);

    public CommonResult<Void> logout();
}