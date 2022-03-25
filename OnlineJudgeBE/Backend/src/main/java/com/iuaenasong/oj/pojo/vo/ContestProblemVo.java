/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "比赛题目列表格式数据ContestProblemVo", description = "")
@Data
public class ContestProblemVo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "该题目在比赛中的顺序id")
    private String displayId;

    @ApiModelProperty(value = "比赛id")
    private Long cid;

    @ApiModelProperty(value = "题目id")
    private Long pid;

    @ApiModelProperty(value = "该题目在比赛中的标题，默认为原名字")
    private String displayTitle;

    @ApiModelProperty(value = "该题目在比赛中的气球颜色")
    private String color;

    @ApiModelProperty(value = "该题目的ac通过数")
    private Integer ac;

    @ApiModelProperty(value = "该题目的总提交数")
    private Integer total;
}