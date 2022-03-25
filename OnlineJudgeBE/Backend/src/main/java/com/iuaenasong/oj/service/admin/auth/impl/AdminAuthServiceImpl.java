/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.auth.impl;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.admin.auth.AdminAuthManager;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.service.admin.auth.AdminAuthService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Autowired
    private AdminAuthManager adminAuthManager;

    @Override
    public CommonResult<IPage<Auth>> getAuthList(Integer limit, Integer currentPage, String keyword) {
        return CommonResult.successResponse(adminAuthManager.getAuthList(limit, currentPage, keyword));
    }

    @Override
    public CommonResult<Void> deleteAuth(Long aid) {
        try {
            adminAuthManager.deleteAuth(aid);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> addAuth(Auth auth) {
        try {
            adminAuthManager.addAuth(auth);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> updateAuth(Auth auth) {
        try {
            adminAuthManager.updateAuth(auth);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> changeAuthStatus(Long aid, Integer status) {
        try {
            adminAuthManager.changeAuthStatus(aid, status);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}
