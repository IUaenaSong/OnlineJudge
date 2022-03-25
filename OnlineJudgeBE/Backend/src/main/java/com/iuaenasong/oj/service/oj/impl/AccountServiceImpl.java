/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj.impl;

import com.iuaenasong.oj.pojo.dto.ChangeMobileDto;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusSystemErrorException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.manager.oj.AccountManager;
import com.iuaenasong.oj.pojo.dto.ChangeEmailDto;
import com.iuaenasong.oj.pojo.dto.ChangePasswordDto;
import com.iuaenasong.oj.pojo.dto.CheckUsernameOrEmailOrMobileDto;
import com.iuaenasong.oj.pojo.vo.ChangeAccountVo;
import com.iuaenasong.oj.pojo.vo.CheckUsernameOrEmailOrMobileVo;
import com.iuaenasong.oj.pojo.vo.UserHomeVo;
import com.iuaenasong.oj.pojo.vo.UserInfoVo;
import com.iuaenasong.oj.service.oj.AccountService;

import javax.annotation.Resource;

@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountManager accountManager;

    @Override
    public CommonResult<CheckUsernameOrEmailOrMobileVo> checkUsernameOrEmailOrMobile(CheckUsernameOrEmailOrMobileDto checkUsernameOrEmailOrMobileDto) {
        return CommonResult.successResponse(accountManager.checkUsernameOrEmailOrMobile(checkUsernameOrEmailOrMobileDto));
    }

    @Override
    public CommonResult<UserHomeVo> getUserHomeInfo(String uid, String username) {
        try {
            return CommonResult.successResponse(accountManager.getUserHomeInfo(uid, username));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<ChangeAccountVo> changePassword(ChangePasswordDto changePasswordDto) {
        try {
            return CommonResult.successResponse(accountManager.changePassword(changePasswordDto));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public CommonResult<ChangeAccountVo> changeEmail(ChangeEmailDto changeEmailDto) {
        try {
            return CommonResult.successResponse(accountManager.changeEmail(changeEmailDto));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public CommonResult<ChangeAccountVo> changeMobile(ChangeMobileDto changeMobileDto) {
        try {
            return CommonResult.successResponse(accountManager.changeMobile(changeMobileDto));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusSystemErrorException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.SYSTEM_ERROR);
        }
    }

    @Override
    public CommonResult<UserInfoVo> changeUserInfo(UserInfoVo userInfoVo) {
        try {
            return CommonResult.successResponse(accountManager.changeUserInfo(userInfoVo));
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}