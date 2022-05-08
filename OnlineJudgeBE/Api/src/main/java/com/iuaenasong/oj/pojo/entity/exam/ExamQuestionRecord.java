/**
 * @Author LengYun
 * @Since 2022/04/16 22:24
 * @Description
 */

package com.iuaenasong.oj.pojo.entity.exam;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value="ExamQuestionRecord对象", description="")
public class ExamQuestionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "考试id")
    private Long eid;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "问题id")
    private Long qid;

    @ApiModelProperty(value = "考试中的问题id")
    private Long eqid;

    @ApiModelProperty(value = "考试中展示的id")
    private String displayId;

    @ApiModelProperty(value = "提交的答案")
    private String submitAnswer;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "具体提交时间")
    private Date submitTime;

    @ApiModelProperty(value = "提交时间，为提交时间减去比赛时间")
    private Long time;

    @ApiModelProperty(value = "结果")
    private Integer status;

    @ApiModelProperty(value = "得分")
    private Integer score;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

}
