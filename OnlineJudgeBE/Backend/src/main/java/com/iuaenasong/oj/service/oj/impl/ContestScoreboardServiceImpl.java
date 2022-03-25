/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj.impl;

import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.manager.oj.ContestScoreboardManager;
import com.iuaenasong.oj.pojo.dto.ContestRankDto;
import com.iuaenasong.oj.pojo.vo.ContestOutsideInfo;
import com.iuaenasong.oj.service.oj.ContestScoreboardService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContestScoreboardServiceImpl implements ContestScoreboardService {

    @Resource
    private ContestScoreboardManager contestScoreboardManager;

    @Override
    public CommonResult<ContestOutsideInfo> getContestOutsideInfo(Long cid) {
        try {
            return CommonResult.successResponse(contestScoreboardManager.getContestOutsideInfo(cid));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<List> getContestOutsideScoreboard(ContestRankDto contestRankDto) {
        try {
            return CommonResult.successResponse(contestScoreboardManager.getContestOutsideScoreboard(contestRankDto));
        }  catch (StatusForbiddenException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}