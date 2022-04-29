/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="考试报名统计", description="")
public class ExamRegisterCountVo implements Serializable {

    @ApiModelProperty(value = "考试id")
    private Long eid;

    @ApiModelProperty(value = "考试报名人数")
    private Integer count;
}
