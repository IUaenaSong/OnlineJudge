/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmailCodeVo {

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "验证码有效时间，单位秒")
    private Integer expire;
}