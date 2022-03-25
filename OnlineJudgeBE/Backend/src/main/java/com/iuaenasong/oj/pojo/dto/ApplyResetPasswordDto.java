/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;

@Data
public class ApplyResetPasswordDto {

    private String captcha;

    private String captchaKey;

    private String email;
}