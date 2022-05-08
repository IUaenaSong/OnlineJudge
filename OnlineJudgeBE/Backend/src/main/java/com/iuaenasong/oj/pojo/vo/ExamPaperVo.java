/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@ApiModel(value = "考试试卷ExamPaperVo", description = "")
@Data
public class ExamPaperVo implements Serializable {

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户真实姓名")
    private String realname;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "总分数")
    private Double score = 0.0;

    @ApiModelProperty(value = "总用时")
    private Integer time = 0;

    private HashMap<String, QuestionAnswerVo> examQuestionRecordList = new HashMap<String, QuestionAnswerVo>();

    private HashMap<String, ProblemAnswerVo> examRecordList = new HashMap<String, ProblemAnswerVo>();
}