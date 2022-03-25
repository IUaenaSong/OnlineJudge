/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContestRankDto {
    
    private Long cid;

    private Integer limit;

    private Integer currentPage;

    private Boolean forceRefresh;

    private Boolean removeStar;

    private List<String> concernedList;
}