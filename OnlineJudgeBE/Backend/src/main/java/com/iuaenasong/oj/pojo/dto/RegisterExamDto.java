/**
 * @Author LengYun
 * @Since 2022/05/01 13:41
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterExamDto {

    @NotBlank(message = "eid不能为空")
    private Long eid;

    @NotBlank(message = "password不能为空")
    private String password;
}