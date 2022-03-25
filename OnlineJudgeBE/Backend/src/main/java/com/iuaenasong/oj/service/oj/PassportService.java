/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ApplyResetPasswordDto;
import com.iuaenasong.oj.pojo.dto.LoginDto;
import com.iuaenasong.oj.pojo.dto.RegisterDto;
import com.iuaenasong.oj.pojo.dto.ResetPasswordDto;
import com.iuaenasong.oj.pojo.vo.EmailCodeVo;
import com.iuaenasong.oj.pojo.vo.MobileCodeVo;
import com.iuaenasong.oj.pojo.vo.RegisterCodeVo;
import com.iuaenasong.oj.pojo.vo.UserInfoVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PassportService {

    public CommonResult<UserInfoVo> login(LoginDto loginDto, HttpServletResponse response, HttpServletRequest request);

    public CommonResult<RegisterCodeVo> getRegisterCode(String email);

    public CommonResult<EmailCodeVo> getEmailCode(String email);

    public CommonResult<MobileCodeVo> getMobileCode(String mobile);

    public CommonResult<Void> register(RegisterDto registerDto);

    public CommonResult<Void> applyResetPassword(ApplyResetPasswordDto applyResetPasswordDto);

    public CommonResult<Void> resetPassword(ResetPasswordDto resetPasswordDto);
}