/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.iuaenasong.oj.pojo.vo.EmailCodeVo;
import com.iuaenasong.oj.pojo.vo.MobileCodeVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.LoginDto;
import com.iuaenasong.oj.pojo.dto.RegisterDto;
import com.iuaenasong.oj.pojo.dto.ApplyResetPasswordDto;
import com.iuaenasong.oj.pojo.dto.ResetPasswordDto;
import com.iuaenasong.oj.pojo.vo.RegisterCodeVo;
import com.iuaenasong.oj.pojo.vo.UserInfoVo;
import com.iuaenasong.oj.service.oj.PassportService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class PassportController {

    @Autowired
    private PassportService passportService;

    
    @PostMapping("/login")
    public CommonResult<UserInfoVo> login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response, HttpServletRequest request) {
        return passportService.login(loginDto, response, request);
    }

    
    @RequestMapping(value = "/get-register-code", method = RequestMethod.GET)
    public CommonResult<RegisterCodeVo> getRegisterCode(@RequestParam(value = "email", required = true) String email) {
        return passportService.getRegisterCode(email);
    }

    @RequestMapping(value = "/get-email-code", method = RequestMethod.GET)
    public CommonResult<EmailCodeVo> getEmailCode(@RequestParam(value = "email", required = true) String email) {
        return passportService.getEmailCode(email);
    }

    @RequestMapping(value = "/get-mobile-code", method = RequestMethod.GET)
    public CommonResult<MobileCodeVo> getMobileCode(@RequestParam(value = "mobile", required = true) String mobile) {
        return passportService.getMobileCode(mobile);
    }

    
    @PostMapping("/register")
    public CommonResult<Void> register(@Validated @RequestBody RegisterDto registerDto) {
        return passportService.register(registerDto);
    }

    
    @PostMapping("/apply-reset-password")
    public CommonResult<Void> applyResetPassword(@RequestBody ApplyResetPasswordDto applyResetPasswordDto) {
        return passportService.applyResetPassword(applyResetPasswordDto);
    }

    
    @PostMapping("/reset-password")
    public CommonResult<Void> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return passportService.resetPassword(resetPasswordDto);
    }

    
    @GetMapping("/logout")
    @RequiresAuthentication
    public CommonResult<Void> logout() {
        SecurityUtils.getSubject().logout();
        return CommonResult.successResponse("登出成功！");
    }

}