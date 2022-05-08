/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ExamQuestionDto {

    @NotBlank(message = "问题id不能为空")
    private Long qid;

    @NotBlank(message = "考试id不能为空")
    private Long eid;

    @NotBlank(message = "问题在考试中的展示id不能为空")
    private String displayId;
}