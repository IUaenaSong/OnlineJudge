/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import lombok.Data;

@Data
public class ChangeAccountVo {

    private Integer code;

    private String msg;

    private UserInfoVo userInfo;
}