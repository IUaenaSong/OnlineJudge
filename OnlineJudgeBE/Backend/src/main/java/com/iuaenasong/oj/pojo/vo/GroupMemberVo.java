/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value="GroupMemberVo", description="")
@Data
public class GroupMemberVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "团队id")
    private Long gid;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "当前状态")
    private Integer auth;

    @ApiModelProperty(value = "申请理由")
    private String reason;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Date gmtModify;
}
