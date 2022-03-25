/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.ContestRankDto;
import com.iuaenasong.oj.pojo.vo.ContestOutsideInfo;

import java.util.List;

public interface ContestScoreboardService {

    public CommonResult<ContestOutsideInfo> getContestOutsideInfo(Long cid);

    public CommonResult<List> getContestOutsideScoreboard(ContestRankDto contestRankDto);

}