/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.exception.AccessException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import com.iuaenasong.oj.common.exception.StatusAccessDeniedException;
import com.iuaenasong.oj.common.exception.StatusFailException;
import com.iuaenasong.oj.common.exception.StatusForbiddenException;
import com.iuaenasong.oj.common.exception.StatusNotFoundException;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.common.result.ResultStatus;
import com.iuaenasong.oj.manager.oj.JudgeManager;
import com.iuaenasong.oj.pojo.dto.SubmitIdListDto;
import com.iuaenasong.oj.pojo.dto.ToJudgeDto;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.judge.JudgeCase;
import com.iuaenasong.oj.pojo.vo.JudgeVo;
import com.iuaenasong.oj.pojo.vo.SubmissionInfoVo;
import com.iuaenasong.oj.service.oj.JudgeService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private JudgeManager judgeManager;

    @Override
    public CommonResult<Judge> submitProblemJudge(ToJudgeDto judgeDto) {
        try {
            return CommonResult.successResponse(judgeManager.submitProblemJudge(judgeDto));
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @Override
    public CommonResult<Judge> resubmit(Long submitId) {
        try {
            return CommonResult.successResponse(judgeManager.resubmit(submitId));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<SubmissionInfoVo> getSubmission(Long submitId) {
        try {
            return CommonResult.successResponse(judgeManager.getSubmission(submitId));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<IPage<JudgeVo>> getJudgeList(Integer limit, Integer currentPage, Boolean onlyMine, String searchPid, Integer searchStatus, String searchUsername, Long gid, Boolean completeProblemID) {
        try {
            return CommonResult.successResponse(judgeManager.getJudgeList(limit, currentPage, onlyMine, searchPid, searchStatus, searchUsername, gid, completeProblemID));
        } catch (StatusAccessDeniedException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.ACCESS_DENIED);
        } catch (AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<Void> updateSubmission(Judge judge) {
        try {
            judgeManager.updateSubmission(judge);
            return CommonResult.successResponse();
        } catch (StatusFailException e) {
            return CommonResult.errorResponse(e.getMessage());
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<HashMap<Long, Object>> checkCommonJudgeResult(SubmitIdListDto submitIdListDto) {
        try {
            return CommonResult.successResponse(judgeManager.checkCommonJudgeResult(submitIdListDto));
        } catch (AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }

    @Override
    public CommonResult<HashMap<Long, Object>> checkContestJudgeResult(SubmitIdListDto submitIdListDto) {
        try {
            return CommonResult.successResponse(judgeManager.checkContestJudgeResult(submitIdListDto));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<HashMap<Long, Object>> checkExamJudgeResult(SubmitIdListDto submitIdListDto) {
        try {
            return CommonResult.successResponse(judgeManager.checkExamJudgeResult(submitIdListDto));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CommonResult<List<JudgeCase>> getALLCaseResult(Long submitId) {
        try {
            return CommonResult.successResponse(judgeManager.getALLCaseResult(submitId));
        } catch (StatusNotFoundException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.NOT_FOUND);
        } catch (StatusForbiddenException | AccessException e) {
            return CommonResult.errorResponse(e.getMessage(), ResultStatus.FORBIDDEN);
        }
    }
}