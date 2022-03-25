/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.account.impl;

import com.iuaenasong.oj.pojo.vo.UserInfoVo;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusAccessDeniedException;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.manager.admin.account.AdminAccountManager;
import com.iuaenasong.oj.service.admin.account.AdminAccountService;
import com.iuaenasong.oj.pojo.dto.LoginDto;

import javax.annotation.Resource;

@Service
public class AdminAccountServiceImpl implements AdminAccountService {

    @Resource
    private AdminAccountManager adminAccountManager;

    @Override
    public CommonResult<UserInfoVo> login(LoginDto loginDto) {
        try {
            return CommonResult.successResponse(adminAccountManager.login(loginDto));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        }
    }

    @Override
    public CommonResult<Void> logout() {
        adminAccountManager.logout();
        return CommonResult.successResponse("退出登录成功！");
    }
}