/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.role.impl;

import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.admin.role.AdminRoleAuthManager;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.pojo.entity.user.RoleAuth;
import com.iuaenasong.oj.service.admin.role.AdminRoleAuthService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleAuthServiceImpl implements AdminRoleAuthService {

    @Autowired
    private AdminRoleAuthManager adminRoleAuthManager;

    @Override
    public CommonResult<IPage<Auth>> getAuthList(Integer limit, Integer currentPage, Long rid, String keyword) {
        return CommonResult.successResponse(adminRoleAuthManager.getAuthList(limit, currentPage, rid, keyword));
    }

    @Override
    public CommonResult<Void> addAuth(RoleAuth roleAuth) {
        try {
            adminRoleAuthManager.addAuth(roleAuth);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteAuth(Long rid, Long aid) {
        try {
            adminRoleAuthManager.deleteAuth(rid, aid);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}
