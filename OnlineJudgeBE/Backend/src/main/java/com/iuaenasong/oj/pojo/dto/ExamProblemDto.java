/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ExamProblemDto {

    @NotBlank(message = "题目id不能为空")
    private Long pid;

    @NotBlank(message = "考试id不能为空")
    private Long eid;

    @NotBlank(message = "题目在考试中的展示id不能为空")
    private String displayId;
}