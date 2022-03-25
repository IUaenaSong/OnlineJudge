/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.admin;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.entity.user.Auth;
import com.iuaenasong.oj.service.admin.auth.AdminAuthService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiresAuthentication
@RequiresRoles("root")
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    @GetMapping("/get-auth-list")
    public CommonResult<IPage<Auth>> getAuthList(@RequestParam(value = "limit", required = false) Integer limit,
                                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                 @RequestParam(value = "keyword", required = false) String keyword) {
        return adminAuthService.getAuthList(limit, currentPage, keyword);
    }

    @DeleteMapping("")
    public CommonResult<Void> deleteAuth(@RequestParam("aid") Long aid) {

        return adminAuthService.deleteAuth(aid);
    }

    @PostMapping("")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> addAuth(@RequestBody Auth auth) {

        return adminAuthService.addAuth(auth);
    }

    @PutMapping("")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Void> updateAuth(@RequestBody Auth auth) {

        return adminAuthService.updateAuth(auth);
    }

    @PutMapping("/change-auth-status")

    public CommonResult<Void> changeAuthStatus(@RequestParam("aid") Long aid, @RequestParam("status") Integer status) {

        return adminAuthService.changeAuthStatus(aid, status);
    }
}
