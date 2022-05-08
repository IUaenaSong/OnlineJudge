/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "考试问题列表格式数据ExamQuestionVo", description = "")
@Data
public class ExamQuestionVo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "该问题在考试中的顺序id")
    private String displayId;

    @ApiModelProperty(value = "考试id")
    private Long eid;

    @ApiModelProperty(value = "问题描述")
    private String description;

    @ApiModelProperty(value = "问题id")
    private Long qid;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "类型")
    private Boolean single;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "通过数")
    private Integer ac;

    @ApiModelProperty(value = "错误数")
    private Integer error;

    @ApiModelProperty(value = "")
    private Double average;
}