/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserReadContestAnnouncementDto {

    private Long cid;

    private List<Long> readAnnouncementList;
}