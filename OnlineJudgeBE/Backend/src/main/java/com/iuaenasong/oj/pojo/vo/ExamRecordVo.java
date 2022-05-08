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
import java.util.Date;

@ApiModel(value="用户在考试的记录", description="")
@Data
public class ExamRecordVo implements Serializable {

    private Long id;

    @ApiModelProperty(value = "考试id")
    private Long eid;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "题目id")
    private Long pid;

    @ApiModelProperty(value = "考试中的题目id")
    private Long epid;

    @ApiModelProperty(value = "考试中展示的id")
    private String displayId;

    @ApiModelProperty(value = "提交id，用于可重判")
    private Long submitId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "学校")
    private String school;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "提交结果，0表示未AC通过不罚时，1表示AC通过，-1为未AC通过算罚时")
    private Integer status;

    @ApiModelProperty(value = "具体提交时间")
    private Date submitTime;

    @ApiModelProperty(value = "提交时间，为提交时间减去考试时间")
    private Long time;

    @ApiModelProperty(value = "考试的得分")
    private Integer score;

    @ApiModelProperty(value = "代码")
    private String code;

    @ApiModelProperty(value = "提交耗时")
    private Integer useTime;

    private Date gmtCreate;

    private Date gmtModified;

}