/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value="训练题单查询对象TrainingVo", description="")
@Data
public class TrainingVo implements Serializable {

    @ApiModelProperty(value = "训练id")
    private Long id;

    @ApiModelProperty(value = "题目标题")
    private String title;

    @ApiModelProperty(value = "训练描述")
    private String description;

    @ApiModelProperty(value = "训练创建者用户名")
    private String author;

    @ApiModelProperty(value = "训练题单权限类型：Public、Private")
    private String auth;

    @ApiModelProperty(value = "训练题单的分类名称")
    private String categoryName;

    @ApiModelProperty(value = "训练题单的分类背景颜色")
    private String categoryColor;

    @ApiModelProperty(value = "训练题单的编号，升序排序")
    private Integer rank;

    @ApiModelProperty(value = "该训练的总题数")
    private Integer problemCount;

    @ApiModelProperty(value = "当前用户已完成训练的题数")
    private Integer acCount;

    @ApiModelProperty(value = "团队ID")
    private Long gid;

    @ApiModelProperty(value = "训练更新时间")
    private Date gmtModified;

}