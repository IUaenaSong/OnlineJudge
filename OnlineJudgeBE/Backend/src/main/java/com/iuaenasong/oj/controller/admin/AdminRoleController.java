/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.admin;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.pojo.entity.user.Role;
import com.iuaenasong.oj.pojo.entity.user.RoleAuth;
import com.iuaenasong.oj.service.admin.role.AdminRoleAuthService;
import com.iuaenasong.oj.service.admin.role.AdminRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresAuthentication
@RequiresRoles("root")
@RequestMapping("/api/admin/role")
public class AdminRoleController {

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private AdminRoleAuthService adminRoleAuthService;

    @GetMapping("/get-role-list")
    public CommonResult<IPage<Role>> getRoleList(@RequestParam(value = "limit", required = false) Integer limit,
                                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                 @RequestParam(value = "keyword", required = false) String keyword) {
        return adminRoleService.getRoleList(limit, currentPage, keyword);
    }

    @GetMapping("/get-auth-list")
    public CommonResult<IPage<Auth>> getAuthList(@RequestParam(value = "limit", required = false) Integer limit,
                                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                 @RequestParam(value = "rid", required = true) Long rid,
                                                 @RequestParam(value = "keyword", required = false) String keyword) {
        return adminRoleAuthService.getAuthList(limit, currentPage, rid, keyword);
    }

    @PostMapping("/auth")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> addAuth(@RequestBody RoleAuth roleAuth) {
        return adminRoleAuthService.addAuth(roleAuth);
    }

    @DeleteMapping("/auth")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> deleteAuth(@RequestParam(value = "rid", required = true) Long rid,
                                         @RequestParam(value = "aid", required = true) Long aid) {
        return adminRoleAuthService.deleteAuth(rid, aid);
    }
}
