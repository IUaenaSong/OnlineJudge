/**
 * @Author LengYun
 * @Since 2022/04/02 16:51
 * @Description
 */

package com.iuaenasong.oj.pojo.entity.question;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Question对象", description="")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "问题的自定义ID 例如（OJ-1000）")
    private String questionId;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "1选择题，2填空题，3简答题")
    private Integer type;

    @ApiModelProperty(value = "选择类型")
    private Boolean single;

    @ApiModelProperty(value = "默认为1公开，2为私有，3为考试中")
    private Integer auth;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "问题答案")
    private String answer;

    @ApiModelProperty(value = "问题选项")
    private String choices;

    @ApiModelProperty(value = "单选答案")
    private Integer radio;

    @ApiModelProperty(value = "判断答案")
    private Boolean judge;

    @ApiModelProperty(value = "公开答案")
    private Boolean share;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "修改题目的管理员用户名")
    private String modifiedUser;

    @ApiModelProperty(value = "全站可见")
    private Boolean isPublic;

    @ApiModelProperty(value = "团队ID")
    private Long gid;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

}
