/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ContestPrintDto {

    @NotBlank(message = "比赛id不能为空")
    private Long cid;

    @NotBlank(message = "打印内容不能为空")
    private String content;
}