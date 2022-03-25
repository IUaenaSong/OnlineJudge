/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.controller.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.iuaenasong.oj.common.result.CommonResult;
import com.iuaenasong.oj.pojo.dto.SubmitIdListDto;
import com.iuaenasong.oj.pojo.dto.ToJudgeDto;
import com.iuaenasong.oj.pojo.entity.judge.Judge;
import com.iuaenasong.oj.pojo.entity.judge.JudgeCase;
import com.iuaenasong.oj.pojo.vo.JudgeVo;
import com.iuaenasong.oj.pojo.vo.SubmissionInfoVo;
import com.iuaenasong.oj.service.oj.JudgeService;
import java.util.*;

@RestController
@RequestMapping("/api")
public class JudgeController {

    @Autowired
    private JudgeService judgeService;

    
    @GetMapping("/get-submission-list")
    public CommonResult<IPage<JudgeVo>> getJudgeList(@RequestParam(value = "limit", required = false) Integer limit,
                                                     @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                                     @RequestParam(value = "onlyMine", required = false) Boolean onlyMine,
                                                     @RequestParam(value = "problemID", required = false) String searchPid,
                                                     @RequestParam(value = "status", required = false) Integer searchStatus,
                                                     @RequestParam(value = "username", required = false) String searchUsername,
                                                     @RequestParam(value = "completeProblemID", defaultValue = "false") Boolean completeProblemID) {

        return judgeService.getJudgeList(limit, currentPage, onlyMine, searchPid, searchStatus, searchUsername, completeProblemID);
    }

    
    @GetMapping("/get-submission-detail")
    public CommonResult<SubmissionInfoVo> getSubmission(@RequestParam(value = "submitId", required = true) Long submitId) {
        return judgeService.getSubmission(submitId);
    }

    @RequiresAuthentication
    @RequiresPermissions("submit")
    @RequestMapping(value = "/submit-problem-judge", method = RequestMethod.POST)
    public CommonResult<Judge> submitProblemJudge(@RequestBody ToJudgeDto judgeDto) {
        return judgeService.submitProblemJudge(judgeDto);
    }

    
    @RequiresAuthentication
    @GetMapping(value = "/resubmit")
    public CommonResult<Judge> resubmit(@RequestParam("submitId") Long submitId) {
        return judgeService.resubmit(submitId);
    }

    
    @PutMapping("/submission")
    @RequiresAuthentication
    public CommonResult<Void> updateSubmission(@RequestBody Judge judge) {
        return judgeService.updateSubmission(judge);
    }

    
    @RequestMapping(value = "/check-submissions-status", method = RequestMethod.POST)
    public CommonResult<HashMap<Long, Object>> checkCommonJudgeResult(@RequestBody SubmitIdListDto submitIdListDto) {
        return judgeService.checkCommonJudgeResult(submitIdListDto);
    }

    
    @RequestMapping(value = "/check-contest-submissions-status", method = RequestMethod.POST)
    @RequiresAuthentication
    public CommonResult<HashMap<Long, Object>> checkContestJudgeResult(@RequestBody SubmitIdListDto submitIdListDto) {
        return judgeService.checkContestJudgeResult(submitIdListDto);
    }

    
    @GetMapping("/get-all-case-result")
    public CommonResult<List<JudgeCase>> getALLCaseResult(@RequestParam(value = "submitId", required = true) Long submitId) {
        return judgeService.getALLCaseResult(submitId);
    }
}