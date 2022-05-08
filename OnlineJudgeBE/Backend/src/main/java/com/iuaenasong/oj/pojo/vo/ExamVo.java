/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value="考试信息", description="")
@Data
public class ExamVo implements Serializable {

    @TableId(value = "考试id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建者用户名")
    private String author;

    @ApiModelProperty(value = "考试标题")
    private String title;

    @ApiModelProperty(value = "考试说明")
    private String description;

    @ApiModelProperty(value = "-1为未开始，0为进行中，1为已结束")
    private Integer status;

    @ApiModelProperty(value = "考试来源，原创为0，克隆考试为考试id")
    private Integer source;

    @ApiModelProperty(value = "0为公开考试，1为私有考试（有密码），2为保护考试")
    private Integer auth;

    @ApiModelProperty("当前服务器系统时间，为了前端统一时间")
    private Date now;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "比赛时长（秒）")
    private Integer duration;

    @ApiModelProperty(value = "是否开启封榜")
    private Boolean realScore;

    @ApiModelProperty(value = "排行榜显示（username、nickname、realname）")
    private String rankShowName;

    @ApiModelProperty(value = "编程题得分方式，Recent、Highest（最近一次提交、最高得分提交）")
    private String rankScoreType;

    @ApiModelProperty(value = "是否公开分数")
    private Boolean autoRealScore;

    @ApiModelProperty(value = "考试的报名人数")
    private Integer count;

    @ApiModelProperty(value = "团队ID")
    private Long gid;
}