/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.admin.role.impl;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.manager.admin.role.AdminRoleManager;
import com.iuaenasong.oj.pojo.entity.user.Role;
import com.iuaenasong.oj.service.admin.role.AdminRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    @Autowired
    private AdminRoleManager adminRoleManager;

    @Override
    public CommonResult<IPage<Role>> getRoleList(Integer limit, Integer currentPage, String keyword) {
        return CommonResult.successResponse(adminRoleManager.getRoleList(limit, currentPage, keyword));
    }
}
