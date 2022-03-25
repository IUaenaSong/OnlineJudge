/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.iuaenasong.oj.pojo.entity.user.Auth;

import java.util.Date;
import java.util.List;

@ApiModel(value="角色以及其对应的权限列表", description="")
@Data
public class RoleAuthsVo {

    @ApiModelProperty(value = "角色id")
    private Long id;

    @ApiModelProperty(value = "角色")
    private String role;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "默认0可用，1不可用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Date gmtModified;

    @ApiModelProperty(value = "权限列表")
    private List<Auth> auths;
}