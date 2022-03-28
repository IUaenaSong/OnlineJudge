/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.SubmitIdListDto;
import com.iuaenasong.oj.pojo.dto.ToJudgeDto;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.judge.JudgeCase;
import com.iuaenasong.oj.pojo.vo.JudgeVo;
import com.iuaenasong.oj.pojo.vo.SubmissionInfoVo;

import java.util.HashMap;
import java.util.List;

public interface JudgeService {

    public CommonResult<Judge> submitProblemJudge(ToJudgeDto judgeDto);

    public CommonResult<Judge> resubmit(Long submitId);

    public CommonResult<SubmissionInfoVo> getSubmission(Long submitId);

    public CommonResult<IPage<JudgeVo>> getJudgeList(Integer limit,
                                                     Integer currentPage,
                                                     Boolean onlyMine,
                                                     String searchPid,
                                                     Integer searchStatus,
                                                     String searchUsername,
                                                     Long gid,
                                                     Boolean completeProblemID);

    public CommonResult<Void> updateSubmission(Judge judge);

    public CommonResult<HashMap<Long, Object>> checkCommonJudgeResult(SubmitIdListDto submitIdListDto);

    public CommonResult<HashMap<Long, Object>> checkContestJudgeResult(SubmitIdListDto submitIdListDto);

    public CommonResult<List<JudgeCase>> getALLCaseResult(Long submitId);
}
