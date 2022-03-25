/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ChangeEmailDto;
import com.iuaenasong.oj.pojo.dto.ChangeMobileDto;
import com.iuaenasong.oj.pojo.dto.ChangePasswordDto;
import com.iuaenasong.oj.pojo.dto.CheckUsernameOrEmailOrMobileDto;
import com.iuaenasong.oj.pojo.vo.ChangeAccountVo;
import com.iuaenasong.oj.pojo.vo.CheckUsernameOrEmailOrMobileVo;
import com.iuaenasong.oj.pojo.vo.UserHomeVo;
import com.iuaenasong.oj.pojo.vo.UserInfoVo;

public interface AccountService {

    public CommonResult<CheckUsernameOrEmailOrMobileVo> checkUsernameOrEmailOrMobile(CheckUsernameOrEmailOrMobileDto checkUsernameOrEmailOrMobileDto);

    public CommonResult<UserHomeVo> getUserHomeInfo(String uid, String username);

    public CommonResult<ChangeAccountVo> changePassword(ChangePasswordDto changePasswordDto);

    public CommonResult<ChangeAccountVo> changeEmail(ChangeEmailDto changeEmailDto);

    public CommonResult<ChangeAccountVo> changeMobile(ChangeMobileDto changeMobileDto);

    public CommonResult<UserInfoVo> changeUserInfo(UserInfoVo userInfoVo);

}
