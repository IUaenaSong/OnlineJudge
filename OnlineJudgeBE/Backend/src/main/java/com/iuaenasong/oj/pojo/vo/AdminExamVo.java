/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel(value="管理考试的回传实体", description="")
@Data
public class AdminExamVo {

    @ApiModelProperty(value = "考试id")
    private Long id;

    @ApiModelProperty(value = "考试创建者id")
    private String uid;

    @ApiModelProperty(value = "考试创建者的用户名")
    private String author;

    @ApiModelProperty(value = "考试标题")
    private String title;

    @ApiModelProperty(value = "考试说明")
    private String description;

    @ApiModelProperty(value = "考试来源，原创为0，克隆考试为考试id")
    private Integer source;

    @ApiModelProperty(value = "0为公开考试，1为私有考试（访问有密码），2为保护考试（提交有密码）")
    private Integer auth;

    @ApiModelProperty(value = "考试密码")
    private String pwd;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "比赛时长（s）")
    private Long duration;

    @ApiModelProperty(value = "编程题是否实时出分")
    private Boolean sealScore;

    @ApiModelProperty(value = "考试结束是否自动公开成绩以及小题分")
    private Boolean autoRealScore;

    @ApiModelProperty(value = "-1为未开始，0为进行中，1为已结束")
    private Integer status;

    @ApiModelProperty(value = "是否可见")
    private Boolean visible;

    @ApiModelProperty(value = "是否打开账号限制")
    private Boolean openAccountLimit;

    @ApiModelProperty(value = "账号限制规则 <prefix>**</prefix><suffix>**</suffix><start>**</start><end>**</end><extra>**</extra>")
    private String accountLimitRule;

    @ApiModelProperty(value = "排行榜显示（username、nickname、realname）")
    private String rankShowName;

    @ApiModelProperty(value = "编程题目得分方式，Recent、Highest（最近一次提交、最高得分提交）")
    private String oiRankScoreType;

    @ApiModelProperty(value = "团队id")
    private Long gid;

    private Date gmtCreate;

    private Date gmtModified;

}