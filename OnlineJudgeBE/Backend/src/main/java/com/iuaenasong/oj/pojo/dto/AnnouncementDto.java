/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;
import com.iuaenasong.oj.pojo.entity.common.Announcement;

import javax.validation.constraints.NotBlank;

@Data
public class AnnouncementDto {
    @NotBlank(message = "比赛id不能为空")
    private Long cid;

    private Announcement announcement;
}