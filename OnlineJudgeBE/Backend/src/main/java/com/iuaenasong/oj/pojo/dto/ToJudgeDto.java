/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;
;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class ToJudgeDto {
    @NotBlank(message = "题目id不能为空")
    private String pid;

    @NotBlank(message = "代码语言选择不能为空")
    private String language;

    @NotBlank(message = "提交的代码不能为空")
    private String code;

    @NotBlank(message = "提交的比赛id所属不能为空，若并非比赛提交，请设置为0")
    private Long cid;

    @NotBlank(message = "提交的考试id所属不能为空，若并非考试提交，请设置为0")
    private Long eid;

    private Long tid;

    private Boolean isRemote;

}