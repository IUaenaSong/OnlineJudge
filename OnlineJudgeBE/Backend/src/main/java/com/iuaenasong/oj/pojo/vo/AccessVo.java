/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccessVo {

    @ApiModelProperty(value = "是否有进入比赛或训练的权限")
    private Boolean access;
}