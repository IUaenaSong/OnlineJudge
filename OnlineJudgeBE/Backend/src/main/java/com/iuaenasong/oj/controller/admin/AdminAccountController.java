/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.admin;

import com.iuaenasong.oj.pojo.vo.UserInfoVo;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;

import com.iuaenasong.oj.pojo.dto.LoginDto;

import com.iuaenasong.oj.service.admin.account.AdminAccountService;

@RestController
@RequestMapping("/api/admin")
public class AdminAccountController {

    @Autowired
    private AdminAccountService adminAccountService;

    @PostMapping("/login")
    public CommonResult<UserInfoVo> login(@Validated @RequestBody LoginDto loginDto){
       return adminAccountService.login(loginDto);
    }

    @GetMapping("/logout")
    @RequiresAuthentication
    @RequiresRoles(value = {"root","admin","problem_admin"},logical = Logical.OR)
    public CommonResult<Void> logout() {
        return adminAccountService.logout();
    }

}