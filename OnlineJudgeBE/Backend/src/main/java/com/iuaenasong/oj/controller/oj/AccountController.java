/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.pojo.dto.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.vo.*;
import com.iuaenasong.oj.service.oj.AccountService;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    
    @RequestMapping(value = "/check-username-or-email-or-mobile", method = RequestMethod.POST)
    public CommonResult<CheckUsernameOrEmailOrMobileVo> checkUsernameOrEmail(@RequestBody CheckUsernameOrEmailOrMobileDto checkUsernameOrEmailOrMobileDto) {
        return accountService.checkUsernameOrEmailOrMobile(checkUsernameOrEmailOrMobileDto);
    }

    
    @GetMapping("/get-user-home-info")
    public CommonResult<UserHomeVo> getUserHomeInfo(@RequestParam(value = "uid", required = false) String uid,
                                                    @RequestParam(value = "username", required = false) String username) {
        return accountService.getUserHomeInfo(uid, username);
    }

    @PostMapping("/change-password")
    @RequiresAuthentication
    public CommonResult<ChangeAccountVo> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return accountService.changePassword(changePasswordDto);
    }

    @PostMapping("/change-email")
    @RequiresAuthentication
    public CommonResult<ChangeAccountVo> changeEmail(@RequestBody ChangeEmailDto changeEmailDto) {
        return accountService.changeEmail(changeEmailDto);
    }

    @PostMapping("/change-mobile")
    @RequiresAuthentication
    public CommonResult<ChangeAccountVo> changeMobile(@RequestBody ChangeMobileDto changeMobileDto) {
        return accountService.changeMobile(changeMobileDto);
    }

    @PostMapping("/change-userInfo")
    @RequiresAuthentication
    public CommonResult<UserInfoVo> changeUserInfo(@RequestBody UserInfoVo userInfoVo) {
        return accountService.changeUserInfo(userInfoVo);
    }

}